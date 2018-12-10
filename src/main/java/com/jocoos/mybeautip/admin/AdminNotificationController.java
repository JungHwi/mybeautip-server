package com.jocoos.mybeautip.admin;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.devices.DeviceRepository;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.notification.Notification;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminNotificationController {

  private final DeviceService deviceService;
  private final MemberRepository memberRepository;
  private final DeviceRepository deviceRepository;

  public AdminNotificationController(DeviceService deviceService,
                                     MemberRepository memberRepository,
                                     DeviceRepository deviceRepository) {
    this.deviceService = deviceService;
    this.memberRepository = memberRepository;
    this.deviceRepository = deviceRepository;
  }

  @GetMapping(value = "/deviceDetails")
  public ResponseEntity<Page<DeviceDetailInfo>> getDeviceDetails(
     @RequestParam(defaultValue = "false") boolean pushable,
     @RequestParam(required = false) String username,
     @RequestParam(defaultValue = "0") int platform,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "100") int size) {

    List<Integer> links = Lists.newArrayList(1, 2, 4);
    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));


    String deviceOs = deviceService.getDeviceOs(platform);
    Page<Device> devices = null;
    if (!Strings.isNullOrEmpty(username)) {
      if (deviceOs != null) {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndOsAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(links, pushable, deviceOs, username, pageable);
      } else {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(links, pushable, username, pageable);
      }
    } else {
      if (deviceOs != null) {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndOsAndCreatedByDeletedAtIsNull(links, pushable, deviceOs, pageable);
      } else {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndCreatedByDeletedAtIsNull(links, pushable, pageable);
      }
    }

    Page<DeviceDetailInfo> details = devices.map(d-> new DeviceDetailInfo(d, new MemberDetailInfo(d.getCreatedBy())));
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @PostMapping("/notifications")
  public ResponseEntity pushNotifications(@RequestBody NotificationRequest request,
                                          BindingResult bindingResult) {

    log.debug("notification request: {}", request);
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Pageable pageable = PageRequest.of(0, request.getSize());
    List<Notification> notifications = Lists.newArrayList();
    Page<Device> devices = null;

    Long memberId = request.getTarget();
    if (memberId!= null) {
      devices = deviceRepository.findByCreatedByIdAndCreatedByPushableAndCreatedByDeletedAtIsNull(memberId, true, pageable);
    } else {
      String deviceOs = deviceService.getDeviceOs(request.getPlatform());
      if (deviceOs == null) {
        devices = deviceRepository.findByCreatedByPushableAndCreatedByDeletedAtIsNull(true, pageable);
      } else {
        devices = deviceRepository.findByCreatedByPushableAndOsAndCreatedByDeletedAtIsNull(true, deviceOs, pageable);
      }
    }

    notifications.addAll(devices.map(d -> new Notification(d.getCreatedBy(), request.getMessage()))
       .stream().collect(Collectors.toList()));

    deviceService.push(notifications);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }


  @Data
  @NoArgsConstructor
  static class NotificationRequest {
    int size = 100;
    @NotNull
    int platform;
    @NotNull
    String message;
    Long target;
  }
}
