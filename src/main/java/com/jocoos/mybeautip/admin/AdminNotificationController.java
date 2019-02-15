package com.jocoos.mybeautip.admin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.notification.event.PushMessage;
import com.jocoos.mybeautip.notification.event.PushMessageRepository;
import com.jocoos.mybeautip.restapi.DeviceController;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminNotificationController {

  private final DeviceService deviceService;
  private final MemberRepository memberRepository;
  private final DeviceRepository deviceRepository;
  private final PushMessageRepository pushMessageRepository;

  public AdminNotificationController(DeviceService deviceService,
                                     MemberRepository memberRepository,
                                     DeviceRepository deviceRepository,
                                     PushMessageRepository pushMessageRepository) {
    this.deviceService = deviceService;
    this.memberRepository = memberRepository;
    this.deviceRepository = deviceRepository;
    this.pushMessageRepository = pushMessageRepository;
  }


  @GetMapping(value = "/deviceDetails")
  public ResponseEntity<Page<DeviceDetailInfo>> getDeviceDetails(
     @RequestParam(defaultValue = "true") boolean pushable,
     @RequestParam(defaultValue = "true") boolean valid,
     @RequestParam(required = false) String username,
     @RequestParam(defaultValue = "0") int platform,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "100") int size) {

    List<Integer> links = Lists.newArrayList(0, 1, 2, 4);
    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "createdAt"));

    String deviceOs = deviceService.getDeviceOs(platform);
    Page<Device> devices = null;
    if (!Strings.isNullOrEmpty(username)) {
      if (deviceOs != null) {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndValidAndOsAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(links, pushable, valid, deviceOs, username, pageable);
      } else {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndValidAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(links, pushable, valid, username, pageable);
      }
    } else {
      if (deviceOs != null) {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndValidAndOsAndCreatedByDeletedAtIsNull(links, pushable, valid, deviceOs, pageable);
      } else {
        devices = deviceRepository.findByCreatedByLinkInAndPushableAndValidAndCreatedByDeletedAtIsNull(links, pushable, valid, pageable);
      }
    }

    Page<DeviceDetailInfo> details = devices.map(d-> new DeviceDetailInfo(d, new MemberDetailInfo(d.getCreatedBy())));
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @GetMapping(value = "/memberDeviceDetails")
  public ResponseEntity<Page<MemberDevicesInfo>> getMemberDeviceDetails(
     @RequestParam(defaultValue = "false") boolean pushable,
     @RequestParam(required = false) String username,
     @RequestParam(defaultValue = "0") int platform,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "100") int size) {

    List<Integer> links = Lists.newArrayList(0, 1, 2, 4);
    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "createdAt"));

    Page<Member> members = null;
    if (!Strings.isNullOrEmpty(username)) {
      members = memberRepository.findByVisibleAndPushableAndUsernameContaining(true, true, username, pageable);
    } else {
      members = memberRepository.findByVisibleAndPushable(true, true, pageable);
    }

    String deviceOs = deviceService.getDeviceOs(platform);
    Page<MemberDevicesInfo> result = members.map(m -> {
      MemberDevicesInfo info = new MemberDevicesInfo(m);
      List<DeviceController.DeviceInfo> devices = Lists.newArrayList();
      List<Device> list = null;

      if (!Strings.isNullOrEmpty(deviceOs)) {
        list = deviceRepository.findByCreatedByIdAndOs(m.getId(), deviceOs);
      } else {
        list = deviceRepository.findByCreatedById(m.getId());
      }

      list.stream().forEach(
         d -> devices.add(new DeviceController.DeviceInfo(d))
      );

      info.setDevices(devices);
      return info;
    });

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/notifications")
  public ResponseEntity pushNotifications(@Valid @RequestBody NotificationRequest request,
                                          BindingResult bindingResult) {

    log.debug("notification request: {}", request);
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Pageable pageable = PageRequest.of(0, request.getSize());
    Page<Device> devices = null;

    Long memberId = request.getTarget();
    if (memberId!= null) {
      devices = deviceRepository.findByCreatedByIdAndPushableAndValid(memberId, true, true, pageable);
    } else {
      String deviceOs = deviceService.getDeviceOs(request.getPlatform());
      if (deviceOs == null) {
        devices = deviceRepository.findByPushableAndValid(true, true, pageable);
      } else {
        devices = deviceRepository.findByPushableAndValidAndOs(true, true, deviceOs, pageable);
      }
    }
    
    devices.forEach(device ->
      deviceService.push(device, new Notification(device.getCreatedBy(), request.getTitle(),
          request.getMessage(), request.getResourceType(), request.getResourceIds())));
    
    pushMessageRepository.save(new PushMessage(request, devices.getContent().size()));
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }


  @Data
  @NoArgsConstructor
  public static class NotificationRequest {
    int size = 100;
    
    @NotNull
    Integer platform;
    
    int category;
  
    @Size(max=30)
    String title;
    String resourceType;
    String resourceIds;
    Long target;
    
    @NotNull
    @Size(max=120)
    String message;
  }
}
