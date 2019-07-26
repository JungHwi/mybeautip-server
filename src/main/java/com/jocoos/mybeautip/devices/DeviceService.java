package com.jocoos.mybeautip.devices;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

import com.jocoos.mybeautip.admin.AdminNotificationController;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.notification.NotificationRepository;
import com.jocoos.mybeautip.notification.event.PushMessage;
import com.jocoos.mybeautip.notification.event.PushMessageRepository;
import com.jocoos.mybeautip.restapi.DeviceController;

@Slf4j
@Service
public class DeviceService {

  private static final String MESSAGE_STRUCTURE = "json";
  private static final String KEY_NOTIFICATION = "notification";
  private static final String KEY_DATA = "data";

  private final MessageService messageService;
  private final DeviceRepository deviceRepository;
  private final NotificationRepository notificationRepository;
  private final PushMessageRepository pushMessageRepository;
  private final ObjectMapper objectMapper;

  @Value("${mybeautip.aws.sns.application.gcm-arn}")
  private String gcmArn;

  @Autowired
  private AmazonSNS amazonSNS;

  public DeviceService(DeviceRepository deviceRepository,
                       MessageService messageService,
                       NotificationRepository notificationRepository,
                       PushMessageRepository pushMessageRepository,
                       ObjectMapper objectMapper,
                       AmazonSNS amazonSNS) {
    this.deviceRepository = deviceRepository;
    this.messageService = messageService;
    this.notificationRepository = notificationRepository;
    this.pushMessageRepository = pushMessageRepository;
    this.objectMapper = objectMapper;
    this.amazonSNS = amazonSNS;
  }
  
  @Transactional
  private Device copyBasicInfo(DeviceController.UpdateDeviceRequest src, Device target) {
    target.setId(src.getDeviceId());
    target.setOs(src.getDeviceOs());
    target.setOsVersion(src.getDeviceOsVersion());
    target.setName(src.getDeviceName());
    target.setLanguage(src.getDeviceLanguage());
    target.setAppVersion(src.getAppVersion());
    target.setTimezone(src.getDeviceTimezone());
    target.setArn(createARN(src.getDeviceId(), src.getDeviceOs()));
    return target;
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Device getDevice(String deviceId) {
    return deviceRepository.findById(deviceId).orElse(null);
  }
  
  @Transactional
  public Device create(DeviceController.UpdateDeviceRequest request, Member me) {
    Device device = copyBasicInfo(request, new Device());
    
    if (request.getPushable() == null) {
      device.setPushable(false);  // default
    } else {
      device.setPushable(request.getPushable());
    }
  
    device.setCreatedBy(me);
    return deviceRepository.save(device);
  }
  
  @Transactional
  public Device update(DeviceController.UpdateDeviceRequest request, Device target, Member me) {
    Device device = copyBasicInfo(request, target);
    
    if (request.getPushable() != null) {
      device.setPushable(request.getPushable());
    }
  
    device.setCreatedBy(me);
    return deviceRepository.save(device);
  }
  
  public boolean isPushable(Device device) {
    Member member = device.getCreatedBy();
    
    if (member == null) {  // guest
      return device.isValid() && device.isPushable();
    } else {
      return device.isValid() && device.isPushable() && member.getPushable();
    }
  }
  
  @Transactional
  public Device releaseDevice(Device device) {
    device.setCreatedBy(null);
    return deviceRepository.save(device);
  }
  
  public void push(Notification notification) {
    List<Device> devices = deviceRepository.findByCreatedByIdAndValidIsTrue(notification.getTargetMember().getId());
    log.debug("{}", devices);

    devices.forEach(d -> {
         if (isPushable(d)) {
           push(d, notification);
         } else {
           log.debug("pushable is false: {}", d);
         }
       });
  }

  public String getDeviceOs(int platform) {
    return platform == 1 ? Device.OS_NAME_IOS :
       platform == 2 ? Device.OS_NAME_ANDROID : null;
  }

  public List<Device> getDevices(int platform) {
    List<Device> devices;

    switch (platform) {
      case 1:
        devices = deviceRepository.findByPushableAndValidAndOs(true, true, Device.OS_NAME_IOS);
        break;
      case 2:
        devices = deviceRepository.findByPushableAndValidAndOs(true, true, Device.OS_NAME_ANDROID);
        break;
      default:
        devices = deviceRepository.findByPushableAndValid(true, true);
    }

    return devices.stream().filter(d -> isPushable(d)).collect(Collectors.toList());
  }


  @Async
  public void pushAll(List<Device> devices, AdminNotificationController.NotificationRequest request) {
    int successCount = 0;
    int failCount = 0;
    
    for (Device device : devices) {
      if (push(device, new Notification(device.getCreatedBy(),
          request.getTitle(), request.getMessage(), request.getResourceType(), request.getResourceIds(), request.getImageUrl()))) {
        successCount++;
      } else {
        failCount++;
      }
    }
    pushMessageRepository.save(new PushMessage(request, devices.size(), successCount, failCount));
  }

  @Transactional
  public boolean push(Device device, Notification notification) {
    String message = convertToGcmMessage(notification, device.getOs());
    log.debug("gcm message: {}", message);

    try {
      PublishRequest request = new PublishRequest()
         .withTargetArn(device.getArn())
         .withMessage(message)
         .withMessageStructure(MESSAGE_STRUCTURE);

      PublishResult result = amazonSNS.publish(request);
      log.debug("result: {}", result);
      return true;
    } catch (AmazonSNSException e) {
      log.info("AmazonSNSException: " + e.getMessage());
      
      // Check device token validity
      if (disabled(device)) {
        device.setValid(false); // invalidate device
        deviceRepository.save(device);
        String username = (device.getCreatedBy() == null) ? "Guest" : device.getCreatedBy().getUsername();
        Long userId = (device.getCreatedBy() == null) ? 0L : device.getCreatedBy().getId();
        log.info(String.format("AmazonSNSException Device invalidated - name: %s, created_by: %s(%d), arn: %s",
            device.getName(), username, userId, device.getArn()));
      }
    }
    return false;
  }

  private String convertToGcmMessage(Notification notification, String os) {
    String message = !Strings.isNullOrEmpty(notification.getInstantMessageBody()) ?
       notification.getInstantMessageBody() :
       messageService.getMessage(notification);

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
      data.put(KEY_DATA, message);
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
  
  @Transactional
  public void checkDevicesValidity(Long memberId) {
    deviceRepository.findByCreatedByIdAndValidIsTrue(memberId)
        .forEach(device -> {
          if (disabled(device)) {
            device.setValid(false); // invalidate device
            deviceRepository.save(device);
            String username = (device.getCreatedBy() == null) ? "Guest" : device.getCreatedBy().getUsername();
            Long userId = (device.getCreatedBy() == null) ? 0L : device.getCreatedBy().getId();
            log.info(String.format("Device invalidated - name: %s, created_by: %s(%d), arn: %s",
                device.getName(), username, userId, device.getArn()));
          }
        });
    }
  
  // Check device token validity through Amazon SNS
  private boolean disabled(Device device) {
    GetEndpointAttributesRequest request = new GetEndpointAttributesRequest().withEndpointArn(device.getArn());
    GetEndpointAttributesResult result = amazonSNS.getEndpointAttributes(request);
    
    if (result != null && result.getAttributes() != null) {
      String value = result.getAttributes().get("Enabled");
      return "false".equalsIgnoreCase(value);
    }
    return false;
  }
}
