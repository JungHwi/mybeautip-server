package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
  public ResponseEntity<NoticeResponse> register(@Valid @RequestBody DeviceController.DeviceInfo request,
                                                 BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    log.debug("request: {}", request);
    deviceService.saveOrUpdate(request);

    List<NoticeInfo> noticeInfos = noticeService.findByOs(request.getDeviceOs(), request.getAppVersion())
       .stream().map(input -> new NoticeInfo(input.getType(), input.getMessage()))
       .collect(Collectors.toList());

    return new ResponseEntity<>(new NoticeResponse(noticeInfos), HttpStatus.OK);
  }

  @Data
  public static class DeviceInfo {

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
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NoticeResponse {
    private List<NoticeInfo> notices = Lists.newArrayList();

    public void add(String type, String message) {
      notices.add(new NoticeInfo(type, message));
    }
  }

  @AllArgsConstructor
  public static class NoticeInfo {
    private String key;
    private String message;
  }
}
