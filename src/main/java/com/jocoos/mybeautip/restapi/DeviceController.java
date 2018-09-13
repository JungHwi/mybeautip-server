package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.devices.NoticeService;
import com.jocoos.mybeautip.exception.BadRequestException;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/devices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {

  private final NoticeService noticeService;
  private final DeviceService deviceService;

  public DeviceController(NoticeService noticeService,
                          DeviceService deviceService) {
    this.noticeService = noticeService;
    this.deviceService = deviceService;
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DeviceInfo> register(@Valid @RequestBody DeviceController.UpdateDeviceRequest request,
                                                 BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    log.debug("request: {}", request);
    Device device = deviceService.saveOrUpdate(request);
    log.debug("saved device: {}", device);

    return new ResponseEntity<>(new DeviceInfo(device), HttpStatus.OK);
  }

  @Data
  public static class UpdateDeviceRequest {

    @NotNull @Size(max = 500)
    private String deviceId;

    @NotNull @Size(max = 10)
    private String deviceOs;

    @NotNull @Size(max = 10)
    private String deviceOsVersion;

    @NotNull @Size(max = 50)
    private String deviceName;

    @NotNull @Size(max = 4)
    private String deviceLanguage;

    @NotNull @Size(max = 10)
    private String appVersion;

    @NotNull @Size(max = 40)
    private String deviceTimezone;

    private boolean pushable;
  }

  @Data
  public static class DeviceInfo {

    private String deviceId;
    private String deviceOs;
    private String deviceOsVersion;
    private String deviceName;
    private String deviceLanguage;
    private String appVersion;
    private String deviceTimezone;
    private boolean pushable;

    public DeviceInfo(Device device) {
      BeanUtils.copyProperties(device, this);
      this.deviceId = device.getId();
      this.deviceOs = device.getOs();
      this.deviceOsVersion = device.getOs();
      this.deviceName = device.getName();
      this.deviceLanguage = device.getLanguage();
      this.deviceTimezone = device.getTimezone();
    }
  }
}
