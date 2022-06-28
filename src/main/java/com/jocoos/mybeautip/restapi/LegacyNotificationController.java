package com.jocoos.mybeautip.restapi;


import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.notification.LegacyNotificationService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.Notification;
import com.jocoos.mybeautip.notification.NotificationRepository;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jocoos.mybeautip.notification.Notification.*;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/notifications")
public class LegacyNotificationController {
    private static final String NOTICE_IMG = "https://mybeautip.s3.ap-northeast-2.amazonaws.com/avatar/img_profile_notice.png";

    private final NotificationRepository notificationRepository;
    private final FollowingRepository followingRepository;
    private final CommentRepository commentRepository;
    private final LegacyMemberService legacyMemberService;
    private final MessageService messageService;
    private final LegacyNotificationService legacyNotificationService;
    private final MentionService mentionService;

    public LegacyNotificationController(NotificationRepository notificationRepository,
                                        FollowingRepository followingRepository,
                                        CommentRepository commentRepository,
                                        LegacyMemberService legacyMemberService,
                                        MessageService messageService,
                                        LegacyNotificationService legacyNotificationService,
                                        MentionService mentionService) {
        this.notificationRepository = notificationRepository;
        this.followingRepository = followingRepository;
        this.commentRepository = commentRepository;
        this.legacyMemberService = legacyMemberService;
        this.messageService = messageService;
        this.legacyNotificationService = legacyNotificationService;
        this.mentionService = mentionService;
    }

    @GetMapping
    public CursorResponse getNotifications(@RequestParam(defaultValue = "20") int count,
                                           @RequestParam(required = false) String cursor,
                                           @RequestParam(defaultValue = "0") int step) {
        PageRequest page = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id"));
        Long memberId = legacyMemberService.currentMemberId();
        List<NotificationInfo> result = new ArrayList<>();

        Slice<Notification> notifications;

        if (StringUtils.isNumeric(cursor)) {
            if (step > 0) {
                notifications = notificationRepository.findByTargetMemberIdAndCreatedAtBefore(memberId, new Date(Long.parseLong(cursor)), page);
            } else {
                notifications = notificationRepository.findByTargetMemberIdAndCreatedAtBeforeAndTypeNot(memberId, new Date(Long.parseLong(cursor)), Notification.SYSTEM_MESSAGE, page);
            }

        } else {
            if (step > 0) {
                notifications = notificationRepository.findByTargetMemberId(memberId, page);
            } else {
                notifications = notificationRepository.findByTargetMemberIdAndTypeNot(memberId, Notification.SYSTEM_MESSAGE, page);
            }
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

                    // some notifications for video which is old don't have second argument
                    if (VIDEO_STARTED.equals(n.getType()) || VIDEO_UPLOADED.equals(n.getType())) {
                        if (n.getArgs().size() == 1) {
                            n.getArgs().add(""); // add second arg
                        }
                    }

                    Set<MentionTag> mentionInfo = null;
                    if (StringUtils.equalsAny(n.getType(), typeWithComment)) {
                        if (n.getArgs().size() > 1) {
                            String[] resources = n.getResourceIds().split(",");

                            if (resources.length >= 2) {
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
                        result.add(new NotificationInfo(n, message, following.get().getId(), legacyMemberService.getMemberInfo(n.getTargetMember()), legacyMemberService.getMemberInfo(n.getResourceOwner()), mentionInfo));
                    } else {
                        result.add(new NotificationInfo(n, message, legacyMemberService.getMemberInfo(n.getTargetMember()), legacyMemberService.getMemberInfo(n.getResourceOwner()), mentionInfo));
                    }
                });

        legacyNotificationService.readAllNotification(memberId);

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
        Long memberId = legacyMemberService.currentMemberId();
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
            if (notification.getImageUrl() == null) {
                this.imageUrl = "";
            }

            if (Notification.SYSTEM_MESSAGE.equals(notification.getType())) {
                this.resourceOwner.setAvatarUrl(NOTICE_IMG);
            }
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
