package com.jocoos.mybeautip.restapi;

import com.google.common.collect.Lists;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.notification.*;

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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jocoos.mybeautip.notification.Notification.*;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/notifications")
public class NotificationController {

  private final NotificationRepository notificationRepository;
  private final FollowingRepository followingRepository;
  private final CommentRepository commentRepository;
  private final MemberService memberService;
  private final MessageService messageService;
  private final NotificationService notificationService;
  private final MentionService mentionService;

  public NotificationController(NotificationRepository notificationRepository,
                                FollowingRepository followingRepository,
                                CommentRepository commentRepository,
                                MemberService memberService,
                                MessageService messageService,
                                NotificationService notificationService,
                                MentionService mentionService) {
    this.notificationRepository = notificationRepository;
    this.followingRepository = followingRepository;
    this.commentRepository = commentRepository;
    this.memberService = memberService;
    this.messageService = messageService;
    this.notificationService = notificationService;
    this.mentionService = mentionService;
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
    String[] typeWithComment = {COMMENT, COMMENT_REPLY, COMMENT_LIKE, MENTION};
    
    notifications
      .forEach(n -> {
        if (StringUtils.equalsAny(n.getType(), typeWithUsername)) {
          if (n.getArgs().size() > 0) {
            n.getArgs().set(0, n.getResourceOwner().getUsername());
          }
        }

        Set<MentionTag> mentionInfo = null;
        if (StringUtils.equalsAny(n.getType(), typeWithComment)) {
          if (n.getArgs().size() > 1) {
            String[] resources = n.getResourceIds().split(",");
            
            if (resources.length >=2) {
              long commentId = Long.parseLong(resources[resources.length - 1]);
              Comment comment = commentRepository.findById(commentId).orElse(null);
              if (comment != null) {
                MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
                mentionInfo = mentionResult.getMentionInfo();
                n.getArgs().set(1, mentionResult.getComment());
              }
            }
          }
        }

        String message = messageService.getMessage(n);
        Optional<Following> following = followingRepository.findByMemberMeIdAndMemberYouId(n.getTargetMember().getId(), n.getResourceOwner().getId());
        if (following.isPresent()) {
          result.add(new NotificationInfo(n, message, following.get().getId(), memberService.getMemberInfo(n.getTargetMember()), memberService.getMemberInfo(n.getResourceOwner()), mentionInfo));
        } else {
          result.add(new NotificationInfo(n, message, memberService.getMemberInfo(n.getTargetMember()), memberService.getMemberInfo(n.getResourceOwner()), mentionInfo));
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
  
  @Deprecated
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
    private Set<MentionTag> mentionInfo;

    public NotificationInfo(Notification notification, String message,
                            MemberInfo targetMember, MemberInfo resourceOwner, Set<MentionTag> mentionInfo) {
      BeanUtils.copyProperties(notification, this);
      this.resourceIds = Stream.of(notification.getResourceIds().split(","))
          .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
      this.message = message;
      this.targetMember = targetMember;
      this.resourceOwner = resourceOwner;
      this.mentionInfo = mentionInfo;
    }

    public NotificationInfo(Notification notification, String message, Long followId,
                            MemberInfo targetMember, MemberInfo resourceOwner, Set<MentionTag> mentionInfo) {
      this(notification, message, targetMember, resourceOwner, mentionInfo);
      this.resourceIds = Stream.of(notification.getResourceIds().split(","))
          .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
      this.followId = followId;
      this.targetMember = targetMember;
      this.resourceOwner = resourceOwner;
      this.mentionInfo = mentionInfo;
    }
  }
}
