package com.jocoos.mybeautip.devices;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.restapi.DeviceController;

@Slf4j
@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;


  @Value("${mybeautip.aws.sns.application.gcm-arn}")
  private String gcmArn;

  @Autowired
  private AmazonSNS amazonSNS;

  public DeviceService(DeviceRepository deviceRepository, AmazonSNS amazonSNS) {
    this.deviceRepository = deviceRepository;
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
        return gcmArn;
      case "ios":
        // TODO: Implement IOS APN
        return gcmArn;
      default:
        throw new IllegalArgumentException("Not supoorted os type - " + os);
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
