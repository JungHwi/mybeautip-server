package com.jocoos.mybeautip.admin;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

  public AdminNotificationController(DeviceService deviceService,
                                     MemberRepository memberRepository) {
    this.deviceService = deviceService;
    this.memberRepository = memberRepository;
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
    Long memberId = request.getTarget();
    if (memberId == 0) {
      List<Integer> links = Lists.newArrayList(1, 2, 4);
      notifications.addAll(
         memberRepository.findByLinkInAndPushableAndDeletedAtIsNull(links, true, pageable)
            .map(m -> new Notification(m, request.getMessage())).stream().collect(Collectors.toList()));
    } else {
      Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
         .orElseThrow(() -> new MemberNotFoundException());
      notifications.add(new Notification(member, request.getMessage()));
    }

    deviceService.push(notifications);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @Data
  @NoArgsConstructor
  static class NotificationRequest {
    int size = 100;
    @NotNull
    Long target;
    @NotNull
    String message;
  }
}
