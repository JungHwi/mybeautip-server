package com.jocoos.mybeautip.devices;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.restapi.DeviceController;

@Slf4j
@Service
public class DeviceService {

  private static final String NOTIFICATION_FORMAT = "notification.%s";
  private static final String MESSAGE_STRUCTURE = "json";
  private final DeviceRepository deviceRepository;
  private final MessageSource messageSource;
  private final ObjectMapper objectMapper;

  @Value("${mybeautip.aws.sns.application.gcm-arn}")
  private String gcmArn;

  @Autowired
  private AmazonSNS amazonSNS;

  public DeviceService(DeviceRepository deviceRepository,
                       MessageSource messageSource,
                       ObjectMapper objectMapper,
                       AmazonSNS amazonSNS) {
    this.deviceRepository = deviceRepository;
    this.messageSource = messageSource;
    this.objectMapper = objectMapper;
    this.amazonSNS = amazonSNS;
  }

  @Transactional
  public Device saveOrUpdate(DeviceController.DeviceInfo info) {
    return deviceRepository.findById(info.getDeviceId())
       .map(device -> {
         copyDevice(info, device);

         log.debug("device: {}", device);
         return deviceRepository.save(device);
       })
       .orElseGet(() -> deviceRepository.save(register(info)));
  }

  public Device register(DeviceController.DeviceInfo info) {
    Device device = new Device(info.getDeviceId());
    copyDevice(info, device);
    device.setArn(createARN(info.getDeviceId(), info.getDeviceOs()));

    log.debug("device: {}", device);
    return device;
  }

  public void push(Notification notification) {
    deviceRepository.findByCreatedBy(notification.getTargetMember().getId())
       .forEach(d -> {
         push(d, notification);
       });
  }

  private void push(Device device, Notification notification) {
    String message = convertToGcmMessage(notification, device.getOs());
    log.debug("message: {}", message);

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
      deviceRepository.delete(device);
    }
  }

  private String convertToGcmMessage(Notification notification, String os) {
    String message = messageSource.getMessage(
      String.format(NOTIFICATION_FORMAT, notification.getType()), notification.getArgs().toArray(), Locale.KOREAN);

    Map<String, String> data = Maps.newHashMap();
    data.put("id", String.valueOf(notification.getId()));
    data.put("type", notification.getType());
    data.put("body", message);
    data.put("resource_type", notification.getResourceType());
    data.put("resource_id", String.valueOf(notification.getResourceId()));
    data.put("member_id", String.valueOf(notification.getResourceOwner()));

    switch (os) {
      case "android": {
        return createPushMessage("data", data);
      }
      case "ios": {
        return createPushMessage("notification", data);
      }
      default: {
        throw new IllegalArgumentException("unknown os type");
      }
    }
  }

  private String createPushMessage(String key, Map<String, String> message) {
    Map<String, String> map = Maps.newHashMap();
    Map<String, Map<String, String>> data = Maps.newHashMap();
    data.put(key, message);

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
  private Device copyDevice(DeviceController.DeviceInfo request, Device device) {
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
