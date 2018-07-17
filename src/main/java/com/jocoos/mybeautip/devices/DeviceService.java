package com.jocoos.mybeautip.devices;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.restapi.DeviceController;

@Slf4j
@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;

  public DeviceService(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
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
    log.debug("device: {}", device);

    /**
     * TODO: create arn
     *
     * device.setArn();
     */

    return device;
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
