package com.jocoos.mybeautip.devices;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.notification.NotificationRepository;
import com.jocoos.mybeautip.restapi.DeviceController;

@Slf4j
@Service
public class DeviceService {

  private static final String MESSAGE_STRUCTURE = "json";
  private static final String KEY_NOTIFICATION = "notification";
  private static final String KEY_DATA = "data";

  private final MemberService memberService;
  private final MessageService messageService;
  private final DeviceRepository deviceRepository;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

  @Value("${mybeautip.aws.sns.application.gcm-arn}")
  private String gcmArn;

  @Autowired
  private AmazonSNS amazonSNS;

  public DeviceService(MemberService memberService,
                       DeviceRepository deviceRepository,
                       MessageService messageService,
                       NotificationRepository notificationRepository,
                       ObjectMapper objectMapper,
                       AmazonSNS amazonSNS) {
    this.memberService = memberService;
    this.deviceRepository = deviceRepository;
    this.messageService = messageService;
    this.notificationRepository = notificationRepository;
    this.objectMapper = objectMapper;
    this.amazonSNS = amazonSNS;
  }

  @Transactional
  public Device saveOrUpdate(DeviceController.UpdateDeviceRequest info) {
    return deviceRepository.findById(info.getDeviceId())
       .map(device -> {
         device = copyDevice(info, device);

         if (memberService.currentMember() == null) {
           device.setValid(true);
           device.setPushable(true);
         }
         
         device.setCreatedBy(memberService.currentMember());

         log.debug("device: {}", device);
         return deviceRepository.save(device);
       })
       .orElseGet(() -> deviceRepository.save(register(info)));
  }
  
  public void disableAllDevices(Long memberId) {
    deviceRepository.findByCreatedById(memberId).forEach(device -> {
      device.setValid(false);
      device.setPushable(false);
      deviceRepository.save(device);
    });
  }

  public Device register(DeviceController.UpdateDeviceRequest info) {
    Device device = new Device(info.getDeviceId());
    device = copyDevice(info, device);
    device.setArn(createARN(info.getDeviceId(), info.getDeviceOs()));

    log.debug("device: {}", device);
    return device;
  }

  public void push(Notification notification) {
    deviceRepository.findByCreatedByIdAndValidIsTrue(notification.getTargetMember().getId())
       .forEach(d -> {
         if (d.isPushable()) {
           push(d, notification);
         } else {
           log.warn("device pushable false: {}", d.getId());
         }
       });
  }

  public String getDeviceOs(int platform) {
    return platform == 1 ? Device.OS_NAME_IOS :
       platform == 2 ? Device.OS_NAME_ANDROID : null;
  }

  public void push(Device device, Notification notification) {
    String message = convertToGcmMessage(notification, device.getOs());
    log.debug("gcm message: {}", message);

    try {
      PublishRequest request = new PublishRequest()
         .withTargetArn(device.getArn())
         .withMessage(message)
         .withMessageStructure(MESSAGE_STRUCTURE);

      PublishResult result = amazonSNS.publish(request);
      log.debug("result: {}", result);
    } catch (AmazonSNSException e) {
      log.info("AmazonSNSException: " + e.getMessage());
      
      if (!isValid(device)) {
        device.setValid(false);
        device.setPushable(false);
        deviceRepository.save(device);
      }
    }
  }

  private String convertToGcmMessage(Notification notification, String os) {
    String message = !Strings.isNullOrEmpty(notification.getInstantMessageBody()) ?
       notification.getInstantMessageBody() : messageService.getNotificationMessage(
       notification.getType(), notification.getArgs().toArray());

    Map<String, String> data = Maps.newHashMap();
    data.put("id", String.valueOf(notification.getId()));
    data.put("type", notification.getType());
    data.put("body", message);
    data.put("resource_type", notification.getResourceType());
    data.put("resource_id", String.valueOf(notification.getResourceId()));
    if (notification.getResourceIds() != null) {
      data.put("resource_ids", String.valueOf(Stream.of(notification.getResourceIds().split(","))
         .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList())));
    }
    if (notification.getResourceOwner() != null) {
      data.put("member_id", String.valueOf(notification.getResourceOwner().getId()));
    }
    if (!Strings.isNullOrEmpty(notification.getImageUrl())) {
      data.put("image", notification.getImageUrl());
    }
  
    if (!Strings.isNullOrEmpty(notification.getInstantMessageTitle())) {
      data.put("title", notification.getInstantMessageTitle());
    } else {
      data.put("title", null);
    }

    log.debug("data: {}", data);
    int badge = 0;
    if (notification.getTargetMember() == null) { // Instant push can send to guest
      log.debug("target_member: guest, badge: 0");
    } else {
      badge = notificationRepository.countByTargetMemberAndReadIsFalse(notification.getTargetMember());
      log.debug("target_member: {}, badge: {}", notification.getTargetMember().getId(), badge);
    }
    switch (os) {
      case "android":
      case "ios":
        return createPushMessage(data, badge, os);
      default: {
        throw new IllegalArgumentException("unknown os type");
      }
    }
  }

  private String createPushMessage(Map<String, String> message, int badge, String os) {
    Map<String, String> map = Maps.newHashMap();
    Map<String, Map<String, String>> data = Maps.newHashMap();
    Map<String, String> notification = Maps.newHashMap();

    notification.put("title", message.get("title"));
    notification.put("body", message.get("body"));
    notification.put("badge", String.valueOf(badge));
    
    if ("android".equals(os)) {
      data.put(KEY_DATA, notification);
    } else {
      data.put(KEY_NOTIFICATION, notification);
      data.put(KEY_DATA, message);
    }
    
    try {
      map.put("GCM", objectMapper.writeValueAsString(data));
      return objectMapper.writeValueAsString(map);
    } catch (IOException e) {
      log.error("failed to write json", e);
    }
    return null;
  }

  private String createARN(String deviceId, String deviceOs) {
      return amazonSNS.createPlatformEndpoint(
             new CreatePlatformEndpointRequest()
                .withToken(deviceId)
                .withPlatformApplicationArn(getArn(deviceOs))
            ).getEndpointArn();
  }

  private String getArn(String os) {
    switch (os) {
      case "android":
      case "ios":
        return gcmArn;
      default:
        throw new IllegalArgumentException("Not supported os type - " + os);
    }
  }
  private Device copyDevice(DeviceController.UpdateDeviceRequest request, Device device) {
    if (device == null) {
      throw new NotFoundException("device_not_found", "Device is null or not found");
    }

    device.setId(request.getDeviceId());
    device.setOs(request.getDeviceOs());
    device.setOsVersion(request.getDeviceOsVersion());
    device.setName(request.getDeviceName());
    device.setLanguage(request.getDeviceLanguage());
    device.setAppVersion(request.getAppVersion());
    device.setTimezone(request.getDeviceTimezone());

    if (request.isPushable()) { // enable device, pushable is set according to Member Info
      device.setValid(true);
      device.setPushable((memberService.currentMember() == null) ? true : memberService.currentMember().getPushable());
    } else {  // disable device
      device.setValid(false);
      device.setPushable(false);
    }
    
    return device;
  }
  
  @Transactional
  public void validateAlreadyRegisteredDevices(Long memberId) {
    deviceRepository.findByCreatedByIdAndValidIsTrue(memberId)
        .forEach(device -> {
          if (!isValid(device)) {
            device.setValid(false);
            device.setPushable(false);
            deviceRepository.save(device);
          }
        });
    }
  
  private boolean isValid(Device device) {
    boolean valid = true;
    GetEndpointAttributesRequest request = new GetEndpointAttributesRequest().withEndpointArn(device.getArn());
    GetEndpointAttributesResult result = amazonSNS.getEndpointAttributes(request);
    if (result != null && result.getAttributes() != null) {
      String value = result.getAttributes().get("Enabled");
      if (StringUtils.isNotEmpty(value)) {
        if ("false".equalsIgnoreCase(value)) {
          valid = false;
        }
      }
    }
    return valid;
  }
}
