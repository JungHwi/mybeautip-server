package com.jocoos.mybeautip.devices;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

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
         copyDevice(info, device);

         device.setCreatedBy(memberService.currentMember());

         log.debug("device: {}", device);
         return deviceRepository.save(device);
       })
       .orElseGet(() -> deviceRepository.save(register(info)));
  }
  
  public void setPushable(Long memberId, boolean pushable) {
    deviceRepository.findByCreatedById(memberId).forEach(device -> {
      device.setPushable(pushable);
      deviceRepository.save(device);
    });
  }

  public Device register(DeviceController.UpdateDeviceRequest info) {
    Device device = new Device(info.getDeviceId());
    copyDevice(info, device);
    device.setArn(createARN(info.getDeviceId(), info.getDeviceOs()));

    log.debug("device: {}", device);
    return device;
  }

  public void push(Notification notification) {
    deviceRepository.findByCreatedById(notification.getTargetMember().getId())
       .forEach(d -> {
         if (d.isPushable()) {
           push(d, notification);
         } else {
           log.warn("device pushable false: {}", d.getId());
         }
       });
  }

  private void push(Device device, Notification notification) {
    String message = convertToGcmMessage(notification, device.getOs());
    log.debug("gcm message: {}", message);

    try {
      PublishRequest request = new PublishRequest()
         .withTargetArn(device.getArn())
         .withMessage(message)
         .withMessageStructure(MESSAGE_STRUCTURE);

      PublishResult result = amazonSNS.publish(request);
      log.debug("result: {}", result);
    } catch (AuthorizationErrorException e) {
      log.error("authorizationErrorException", e);
    } catch (PlatformApplicationDisabledException e) {
      log.error("platformApplicationDisabledException", e);
    } catch (EndpointDisabledException e) {
      log.error("EndpointDisabledException", e);

      /**
       * FIXME: Change pushable false or delete device info?
       * deviceRepository.delete(device);
       */
      if (device.isPushable()) {
        device.setPushable(false);
        deviceRepository.save(device);
      }
    }
  }

  private String convertToGcmMessage(Notification notification, String os) {
    String message = messageService.getNotificationMessage(
       notification.getType(), notification.getArgs().toArray());

    Map<String, String> data = Maps.newHashMap();
    data.put("id", String.valueOf(notification.getId()));
    data.put("type", notification.getType());
    data.put("body", message);
    data.put("resource_type", notification.getResourceType());
    data.put("resource_id", String.valueOf(notification.getResourceId()));
    data.put("member_id", String.valueOf(notification.getResourceOwner().getId()));
    if (!Strings.isNullOrEmpty(notification.getImageUrl())) {
      data.put("image", notification.getImageUrl());
    }


    int badge = notificationRepository.countByTargetMemberAndReadIsFalse(notification.getTargetMember());
    log.debug("target_member: {}, badge: {}", notification.getTargetMember().getId(), badge);
    switch (os) {
      case "android": {
        return createPushMessage(data, badge);
      }
      case "ios": {
        return createPushMessage(data, badge);
      }
      default: {
        throw new IllegalArgumentException("unknown os type");
      }
    }
  }

  private String createPushMessage(Map<String, String> message, int badge) {
    Map<String, String> map = Maps.newHashMap();
    Map<String, Map<String, String>> data = Maps.newHashMap();
    Map<String, String> notification = Maps.newHashMap();

    notification.put("title", messageService.getNotificationMessage("title", null));
    notification.put("body", message.get("body"));
    notification.put("badge", String.valueOf(badge));

    data.put(KEY_NOTIFICATION, notification);
    data.put(KEY_DATA, message);

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
      throw new NotFoundException("device not found", "Device is null or not found");
    }

    device.setId(request.getDeviceId());
    device.setOs(request.getDeviceOs());
    device.setOsVersion(request.getDeviceOsVersion());
    device.setName(request.getDeviceName());
    device.setLanguage(request.getDeviceLanguage());
    device.setAppVersion(request.getAppVersion());
    device.setTimezone(request.getDeviceTimezone());
    device.setPushable(request.isPushable());

    return device;
  }
}
