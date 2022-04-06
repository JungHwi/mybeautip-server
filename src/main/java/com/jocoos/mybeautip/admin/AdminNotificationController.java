package com.jocoos.mybeautip.admin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.devices.DeviceRepository;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.restapi.DeviceController;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminNotificationController {

  private final DeviceService deviceService;
  private final MemberRepository memberRepository;
  private final DeviceRepository deviceRepository;
  private final VideoRepository videoRepository;
  private final FollowingRepository followingRepository;

  public AdminNotificationController(DeviceService deviceService,
                                     MemberRepository memberRepository,
                                     DeviceRepository deviceRepository,
                                     VideoRepository videoRepository,
                                     FollowingRepository followingRepository) {
    this.deviceService = deviceService;
    this.memberRepository = memberRepository;
    this.deviceRepository = deviceRepository;
    this.videoRepository = videoRepository;
    this.followingRepository = followingRepository;
  }


  @GetMapping(value = "/deviceDetails")
  public ResponseEntity<Page<DeviceDetailInfo>> getDeviceDetails(
     @RequestParam(defaultValue = "true") boolean pushable,
     @RequestParam(defaultValue = "true") boolean valid,
     @RequestParam(required = false) String username,
     @RequestParam(defaultValue = "0") int platform,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "100") int size) {

    List<Integer> links = Arrays.asList(0, 1, 2, 4);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    String deviceOs = deviceService.getDeviceOs(platform);
    Page<Device> devices = null;
    if (!StringUtils.isBlank(username)) {
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

    List<Integer> links = Arrays.asList(0, 1, 2, 4);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Member> members = null;
    if (!StringUtils.isBlank(username)) {
      members = memberRepository.findByVisibleAndPushableAndUsernameContaining(true, true, username, pageable);
    } else {
      members = memberRepository.findByVisibleAndPushable(true, true, pageable);
    }

    String deviceOs = deviceService.getDeviceOs(platform);
    Page<MemberDevicesInfo> result = members.map(m -> {
      MemberDevicesInfo info = new MemberDevicesInfo(m);
      List<DeviceController.DeviceInfo> devices = new ArrayList<>();
      List<Device> list = null;

      if (!StringUtils.isBlank(deviceOs)) {
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

    List<Device> devices;

    Long memberId = request.getTarget();
    if (memberId!= null) {
      devices = deviceRepository.findByCreatedByIdAndPushableAndValidAndCreatedByPushable(memberId, true, true, true);
    } else {
      devices = deviceService.getDevices(request.getPlatform());
    }
  
    log.info("instant push send start, count: {}", devices.size());
    
    deviceService.pushAll(devices, request);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }


  @Data
  @NoArgsConstructor
  public static class NotificationRequest {
    @NotNull
    Integer platform;
    
    int category;
  
    @Size(max=30)
    String title;
    
    String resourceType;
    String resourceIds;
    String imageUrl;
    Long target;
    
    @NotNull
    @Size(max=120)
    String message;
  }
}
