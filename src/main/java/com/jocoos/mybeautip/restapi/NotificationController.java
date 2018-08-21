package com.jocoos.mybeautip.restapi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.notification.NotificationRepository;
import com.jocoos.mybeautip.notification.NotificationService;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/notifications")
public class NotificationController {
  // TODO: Default avatar URL
  private static final String DEFAULT_AVATAR = "";

  private final NotificationService notificationService;
  private final NotificationRepository notificationRepository;
  private final MemberService memberService;
  private final MessageSource messageSource;

  public NotificationController(NotificationService notificationService,
                                NotificationRepository notificationRepository,
                                MemberService memberService,
                                MessageSource messageSource) {
    this.notificationService = notificationService;
    this.notificationRepository = notificationRepository;
    this.memberService = memberService;
    this.messageSource = messageSource;
  }

  @GetMapping
  public CursorResponse getNotifications(@RequestParam(defaultValue = "20") int count) {
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Long memberId = memberService.currentMemberId();
    List<NotificationInfo> result = Lists.newArrayList();
    notificationRepository.findByTargetMemberId(memberId, page)
       .forEach(n -> {
         log.debug("n: {}", n);
         String message = messageSource.getMessage(n.getType(), n.getArgs().toArray(), Locale.KOREAN);
         log.debug("message: {}", message);
         result.add(new NotificationInfo(n, message));
       });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/notifications", result)
       .withCount(count).
          withCursor(nextCursor).toBuild();
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
    private Member sourceMember;
    private Member targetMember;
    private String type;
    private boolean read;
    private String resourceType;
    private Long resourceId;
    private Long resourceOwner;
    private String imageUrl;
    private String message;
    private Date createdAt;

    public NotificationInfo(Notification notification, String message) {
      BeanUtils.copyProperties(notification, this);
      this.message = message;
      this.imageUrl = !Strings.isNullOrEmpty(notification.getImageUrl()) ? imageUrl : DEFAULT_AVATAR;
    }
  }
}
