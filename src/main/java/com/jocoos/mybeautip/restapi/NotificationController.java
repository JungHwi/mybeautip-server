package com.jocoos.mybeautip.restapi;

import com.google.common.collect.Lists;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.notification.NotificationRepository;
import com.jocoos.mybeautip.notification.NotificationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jocoos.mybeautip.notification.Notification.*;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/notifications")
public class NotificationController {

  private final NotificationRepository notificationRepository;
  private final FollowingRepository followingRepository;
  private final MemberService memberService;
  private final MessageService messageService;
  private final NotificationService notificationService;
  

  public NotificationController(NotificationRepository notificationRepository,
                                FollowingRepository followingRepository,
                                MemberService memberService,
                                MessageService messageService,
                                NotificationService notificationService) {
    this.notificationRepository = notificationRepository;
    this.followingRepository = followingRepository;
    this.memberService = memberService;
    this.messageService = messageService;
    this.notificationService = notificationService;
  }

  @GetMapping
  public CursorResponse getNotifications(@RequestParam(defaultValue = "20") int count,
                                         @RequestParam(required = false) String cursor) {
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Long memberId = memberService.currentMemberId();
    List<NotificationInfo> result = Lists.newArrayList();

    Slice<Notification> notifications;

    if (StringUtils.isNumeric(cursor)) {
      notifications = notificationRepository.findByTargetMemberIdAndCreatedAtBefore(memberId, new Date(Long.parseLong(cursor)), page);
    } else {
      notifications = notificationRepository.findByTargetMemberId(memberId, page);
    }
    
    String[] typeWithUsername = {FOLLOWING, VIDEO_STARTED, VIDEO_UPLOADED, VIDEO_LIKE, COMMENT, COMMENT_REPLY, COMMENT_LIKE, MENTION};
    
    notifications
      .forEach(n -> {
        if (StringUtils.equalsAny(n.getType(), typeWithUsername)) {
          if (n.getArgs().size() > 0) {
            n.getArgs().set(0, n.getResourceOwner().getUsername());
          }
        }
        
        String message = messageService.getNotificationMessage(n.getType(), n.getArgs().toArray());
        Optional<Following> following = followingRepository.findByMemberMeIdAndMemberYouId(n.getTargetMember().getId(), n.getResourceOwner().getId());
        if (following.isPresent()) {
          result.add(new NotificationInfo(n, message, following.get().getId(), memberService.getMemberInfo(n.getTargetMember()), memberService.getMemberInfo(n.getResourceOwner())));
        } else {
          result.add(new NotificationInfo(n, message, memberService.getMemberInfo(n.getTargetMember()), memberService.getMemberInfo(n.getResourceOwner())));
        }
      });
    
    notificationService.readAllNotification(memberId);

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/notifications", result)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  @PatchMapping("/{id:.+}") // Deprecated
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
    private MemberInfo targetMember;
    private String type;
    private boolean read;
    private String resourceType;
    private Long resourceId;
    private List<Long> resourceIds;
    private MemberInfo resourceOwner;
    private String imageUrl;
    private String message;
    private Date createdAt;
    private Long followId;
    private List<MentionTag> mentionInfo;

    public NotificationInfo(Notification notification, String message, MemberInfo targetMember, MemberInfo resourceOwner) {
      BeanUtils.copyProperties(notification, this);
      this.resourceIds = Stream.of(notification.getResourceIds().split(","))
          .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
      this.message = message;
      this.targetMember = targetMember;
      this.resourceOwner = resourceOwner;
    }

    public NotificationInfo(Notification notification, String message, Long followId, MemberInfo targetMember, MemberInfo resourceOwner) {
      this(notification, message, targetMember, resourceOwner);
      this.resourceIds = Stream.of(notification.getResourceIds().split(","))
          .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
      this.followId = followId;
      this.targetMember = targetMember;
      this.resourceOwner = resourceOwner;
    }
  }
}
