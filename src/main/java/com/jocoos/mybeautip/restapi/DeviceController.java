package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
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
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.devices.DeviceRepository;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/devices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {

  private final DeviceService deviceService;
  private final MemberService memberService;
  private final DeviceRepository deviceRepository;

  public DeviceController(DeviceService deviceService,
                          MemberService memberService,
                          DeviceRepository deviceRepository) {
    this.deviceService = deviceService;
    this.memberService = memberService;
    this.deviceRepository = deviceRepository;
  }

  @Transactional
  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DeviceInfo> register(@Valid @RequestBody DeviceController.UpdateDeviceRequest request,
                                             BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
    
    Member me = memberService.currentMember();
    if (me != null) {
    // Check member's devices validity
      deviceService.checkDevicesValidity(me.getId());
    }

    log.debug("request: {}, member: {}", request, me);
    
    Device device = deviceRepository.findById(request.getDeviceId()).orElse(null);
    Device result;
    if (device == null) {
      result = deviceService.create(request, me);
    } else {
      result = deviceService.update(request, device, me);
    }
    
    DeviceInfo info = new DeviceInfo(result);
    log.debug("result device: {}", info);
    return new ResponseEntity<>(new DeviceInfo(result), HttpStatus.OK);
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

    private Boolean pushable;
  }

  @NoArgsConstructor
  @Data
  public static class DeviceInfo {

    private String deviceId;
    private String deviceOs;
    private String deviceOsVersion;
    private String deviceName;
    private String deviceLanguage;
    private String appVersion;
    private String deviceTimezone;
    private Boolean pushable;

    public DeviceInfo(Device device) {
      BeanUtils.copyProperties(device, this);
      this.deviceId = device.getId();
      this.deviceOs = device.getOs();
      this.deviceOsVersion = device.getOsVersion();
      this.deviceName = device.getName();
      this.deviceLanguage = device.getLanguage();
      this.deviceTimezone = device.getTimezone();
    }
  }
}
