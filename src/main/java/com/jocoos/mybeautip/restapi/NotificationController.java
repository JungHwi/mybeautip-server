package com.jocoos.mybeautip.restapi;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.notification.NotificationRepository;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/notifications")
public class NotificationController {

  private final NotificationRepository notificationRepository;
  private final FollowingRepository followingRepository;
  private final MemberService memberService;
  private final MessageService messageService;

  public NotificationController(NotificationRepository notificationRepository,
                                FollowingRepository followingRepository,
                                MemberService memberService,
                                MessageService messageService) {
    this.notificationRepository = notificationRepository;
    this.followingRepository = followingRepository;
    this.memberService = memberService;
    this.messageService = messageService;
  }

  @GetMapping
  public CursorResponse getNotifications(@RequestParam(defaultValue = "20") int count,
                                         @RequestParam(required = false) String cursor) {
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Long memberId = memberService.currentMemberId();
    List<NotificationInfo> result = Lists.newArrayList();

    Slice<Notification> notifications = null;

    if (StringUtils.isNumeric(cursor)) {
      notifications = notificationRepository.findByTargetMemberIdAndCreatedAtBefore(memberId, new Date(Long.parseLong(cursor)), page);
    } else {
      notifications = notificationRepository.findByTargetMemberId(memberId, page);
    }

    notifications
       .forEach(n -> {
         String message = messageService.getNotificationMessage(n.getType(), n.getArgs().toArray());
         Optional<Following> following = followingRepository.findByMemberMeIdAndMemberYouId(n.getTargetMember().getId(), n.getResourceOwner().getId());
         if (following.isPresent()) {
           result.add(new NotificationInfo(n, message, following.get().getId()));
         } else {
           result.add(new NotificationInfo(n, message));
         }

         readNotification(n);
       });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/notifications", result)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  private void readNotification(Notification notification) {
    if (notification == null) {
      return;
    }

    if (!notification.isRead()) {
      notification.setRead(true);
      notificationRepository.save(notification);
    }
  }

  @PatchMapping("/{id:.+}")
  public ResponseEntity readNotification(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    return notificationRepository.findByIdAndTargetMemberId(id, memberId)
       .map(n -> {
         n.setRead(true);
         notificationRepository.save(n);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("notification_not_found", "invalid notification id"));
  }

  @Data
  public static class NotificationInfo {
    private Long id;
    private Member targetMember;
    private String type;
    private boolean read;
    private String resourceType;
    private Long resourceId;
    private Member resourceOwner;
    private String imageUrl;
    private String message;
    private Date createdAt;
    private Long followId;

    public NotificationInfo(Notification notification, String message) {
      BeanUtils.copyProperties(notification, this);
      this.message = message;
    }

    public NotificationInfo(Notification notification, String message, Long followId) {
      this(notification, message);
      this.followId = followId;
    }
  }
}
