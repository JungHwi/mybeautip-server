package com.jocoos.mybeautip.notification;

import java.util.*;
import java.util.stream.Collectors;

import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.recommendation.MemberRecommendationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@Service
public class NotificationService {
  private final DeviceService deviceService;
  private final VideoRepository videoRepository;
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final FollowingRepository followingRepository;
  private final NotificationRepository notificationRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final InstantMessageService instantMessageService;

  @Value("${mybeautip.notification.duplicate-limit-duration}")
  private int duration;

  public NotificationService(DeviceService deviceService,
                             VideoRepository videoRepository,
                             CommentRepository commentRepository,
                             PostRepository postRepository,
                             FollowingRepository followingRepository,
                             NotificationRepository notificationRepository,
                             MemberRepository memberRepository,
                             MemberService memberService,
                             MemberRecommendationRepository memberRecommendationRepository,
                             InstantMessageService instantMessageService) {
    this.deviceService = deviceService;
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.followingRepository = followingRepository;
    this.notificationRepository = notificationRepository;
    this.memberRepository = memberRepository;
    this.memberService = memberService;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.instantMessageService = instantMessageService;
  }

  public void notifyCreateVideo(Video video) {
    Long creator = video.getMember().getId();

    List<Following> followingList = followingRepository.findByCreatedAtBeforeAndMemberYouId(new Date(), creator);
    followingList
       .forEach(f -> {
         Notification notification = new Notification(video, video.getThumbnailUrl(), f.getMemberMe());
         log.debug("notification: {}", notification);

         notificationRepository.save(notification);
         deviceService.push(notification);
       });

    // if creator is recommended member
    List<Member> excludes = followingList.stream().map(Following::getMemberMe).collect(Collectors.toList());
    excludes.add(video.getMember());
    memberRecommendationRepository.findByMemberId(creator)
            .ifPresent(r -> instantMessageService.instantPushMessage(video, excludes));
  }

  public void notifyUploadedMyVideo(Video video) {
    Notification notification = new Notification(video);
    notificationRepository.save(notification);
    deviceService.push(notification);
  }

  public void notifyFollowMember(Following following) {
    Notification notification = followingRepository.findByMemberMeIdAndMemberYouId(
       following.getMemberYou().getId(), following.getMemberMe().getId())
         .map(f -> new Notification(following, f.getId()))
         .orElseGet(() -> new Notification(following, null));
    
    int count = notificationRepository.countByTypeAndTargetMemberAndResourceIdAndResourceOwnerAndCreatedAtAfter(
        Notification.FOLLOWING,
        notification.getTargetMember(),
        notification.getResourceId(),
        notification.getResourceOwner(),
        new Date(System.currentTimeMillis() - duration));
    
    if (count == 0) {
      Notification n = notificationRepository.save(notification);
      log.debug("notification: {}", n);
      deviceService.push(n);
    }
  }

  public void notifyAddComment(Comment comment) {
    if (comment.getPostId() != null) {
      notifyAddPostComment(comment);
    }

    if (comment.getVideoId() != null) {
      notifyAddVideoComment(comment);
    }
  }

  private void notifyAddPostComment(Comment comment) {
    postRepository.findById(comment.getPostId())
       .ifPresent(post -> {
         Notification n = null;
         if (comment.getParentId() != null) {
           Member parent = findCommentMemberByParentId(comment.getParentId());
           n = notificationRepository.save(new Notification(post, comment, comment.getParentId(), parent, post.getThumbnailUrl()));
         } else {
           if (!(comment.getCreatedBy().getId().equals(post.getCreatedBy().getId()))) {
             n = notificationRepository.save(new Notification(post, comment, post.getCreatedBy(), post.getThumbnailUrl()));
           }
         }

         if (n != null) {
           deviceService.push(n);
         }
       });
  }

  private void notifyAddVideoComment(Comment comment) {
    videoRepository.findById(comment.getVideoId())
       .ifPresent(v -> {
         Notification n = null;
         if (comment.getParentId() != null) {
           Member parent = findCommentMemberByParentId(comment.getParentId());
           n = notificationRepository.save(new Notification(v, comment, comment.getParentId(), parent, v.getThumbnailUrl()));
         } else {
           if (!(comment.getCreatedBy().getId().equals(v.getMember().getId()))) {
             n = notificationRepository.save(new Notification(v, comment, v.getMember(), v.getThumbnailUrl()));
           }
         }

         if (n != null) {
           deviceService.push(n);
         }
       });
  }

  private Member findCommentMemberByParentId(Long parentId) {
    return commentRepository.findById(parentId)
       .map(Comment::getCreatedBy)
       .orElse(null);
  }

  public void notifyAddCommentLike(CommentLike commentLike) {
    if (!(commentLike.getCreatedBy().getId().equals(commentLike.getComment().getCreatedBy().getId()))) {
      Notification n = null;
      if (commentLike.getComment().getVideoId() != null) {
        Video video = videoRepository.findById(commentLike.getComment().getVideoId())
            .orElseThrow(() -> new NotFoundException("video_not_found", "Video not found: " + commentLike.getComment().getVideoId()));
  
        int count = notificationRepository.countByTypeAndTargetMemberAndResourceIdAndResourceOwnerAndCreatedAtAfter(
            Notification.COMMENT_LIKE,
            commentLike.getComment().getCreatedBy(),
            commentLike.getComment().getId(),
            commentLike.getCreatedBy(),
            new Date(System.currentTimeMillis() - duration));
        
        if (count == 0) {
          n = notificationRepository.save(new Notification(video, commentLike, commentLike.getComment().getComment()));
  
          log.debug("commentlike video notification: {}", n);
          if (n.getArgs().size() > 1) {
            String original = n.getArgs().get(1);
            if (original.contains("@")) {
              MentionResult mentionResult = createMentionComment(original);
              if (mentionResult != null) {
                n.getArgs().set(1, mentionResult.getComment());
              }
            }
          }
          deviceService.push(n);
        }
      }

      if (commentLike.getComment().getPostId() != null) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(commentLike.getComment().getPostId())
            .orElseThrow(() -> new NotFoundException("post_not_found", "Post not found: " + commentLike.getComment().getPostId()));
  
        int count = notificationRepository.countByTypeAndTargetMemberAndResourceIdAndResourceOwnerAndCreatedAtAfter(
            Notification.COMMENT_LIKE,
            commentLike.getComment().getCreatedBy(),
            commentLike.getComment().getId(),
            commentLike.getCreatedBy(),
            new Date(System.currentTimeMillis() - duration));
  
        if (count == 0) {
          n = notificationRepository.save(new Notification(post, commentLike, commentLike.getComment().getComment()));
          log.debug("commentlike post notification: {}", n);
          if (n.getArgs().size() > 1) {
            String original = n.getArgs().get(1);
            if (original.contains("@")) {
              MentionResult mentionResult = createMentionComment(original);
              if (mentionResult != null) {
                n.getArgs().set(1, mentionResult.getComment());
              }
            }
          }
          deviceService.push(n);
        }
      }
    }
  }

  public void notifyAddVideoLike(VideoLike videoLike) {
    if (!(videoLike.getCreatedBy().getId().equals(videoLike.getVideo().getMember().getId()))) {
      int count = notificationRepository.countByTypeAndTargetMemberAndResourceIdAndResourceOwnerAndCreatedAtAfter(
          Notification.VIDEO_LIKE,
          videoLike.getVideo().getMember(),
          videoLike.getVideo().getId(),
          videoLike.getCreatedBy(),
          new Date(System.currentTimeMillis() - duration));
      
      if (count == 0) {
        Notification n = notificationRepository.save(new Notification(videoLike, videoLike.getCreatedBy()));
        deviceService.push(n);
      }
    }
  }

  public void notifyAddCommentWithMention(Comment comment, Member... mentioned) {
    if (comment.getPostId() == null && comment.getVideoId() == null) {
      log.error("A comment has any post or video id. {}", comment);
    }

    if (comment.getPostId() != null) {
      notifyAddMentionPostComment(comment, mentioned);
    }

    if (comment.getVideoId() != null) {
      notifyAddMentionVideoComment(comment, mentioned);
    }
  }

  private void notifyAddMentionPostComment(Comment comment, Member[] mentioned) {
    postRepository.findById(comment.getPostId())
       .ifPresent(post -> {
         if (mentioned != null) {
           Arrays.stream(mentioned).forEach(m -> {
             if (!(comment.getCreatedBy().getId().equals(m.getId()))) {
               Notification n = notificationRepository.save(new Notification(post, comment, m));
               log.debug("mentioned post comment: {}", n);
               if (n.getArgs().size() > 1) {
                 String original = n.getArgs().get(1);
                 if (original.contains("@")) {
                   MentionResult mentionResult = createMentionComment(original);
                   if (mentionResult != null) {
                     n.getArgs().set(1, mentionResult.getComment());
                   }
                 }
               }
               deviceService.push(n);
             }
           });
         } else {
           notifyAddPostComment(comment);
         }
       });
  }

  private void notifyAddMentionVideoComment(Comment comment, Member[] mentioned) {
    videoRepository.findById(comment.getVideoId())
       .ifPresent(v -> {
         v.setThumbnailUrl(v.getThumbnailUrl());
         if (mentioned != null) {
           Arrays.stream(mentioned).forEach(m -> {
             if (!(comment.getCreatedBy().getId().equals(m.getId()))) {
               Notification n = notificationRepository.save(new Notification(v, comment, m));
               log.debug("mentioned video comment: {}", n);
               if (n.getArgs().size() > 1) {
                 String original = n.getArgs().get(1);
                 if (original.contains("@")) {
                   MentionResult mentionResult = createMentionComment(original);
                   if (mentionResult != null) {
                     n.getArgs().set(1, mentionResult.getComment());
                   }
                 }
               }
               deviceService.push(n);
             }
           });
         } else {
           notifyAddVideoComment(comment);
         }
       });
  }
  
  public void readAllNotification(Long memberId) {
    notificationRepository.findByTargetMemberIdAndReadIsFalse(memberId)
        .forEach(notification -> {
          notification.setRead(true);
          notificationRepository.save(notification);
        });
  }
  
  private String createMentionTag(Object username) {
    return "@" + username + " ";
  }
  
  private Set<String> findMentionTags(String comment) {
    return Arrays.stream(comment.split(" "))
        .filter(c -> c.startsWith("@"))
        .map(c -> c.substring(1))
        .collect(Collectors.toSet());
  }
  
  private MentionResult createMentionComment(String original) {
    MentionResult mentionResult = new MentionResult();
    
    String comment = original + " ";
    Set<String> mentions = findMentionTags(original);
    for (String memberId : mentions) {
      log.debug("member: {}", memberId);
      
      if (StringUtils.isNumeric(memberId)) {
        Optional<Member> member = memberRepository.findById(Long.parseLong(memberId));
        if (member.isPresent()) {
          Member m = member.get();
          mentionResult.add(new MentionTag(m));
          comment = comment.replaceAll(createMentionTag(m.getId()), createMentionTag(m.getUsername()));
        }
      }
      comment = comment.trim();
    }
    
    log.debug("original comment: {}", comment);
    mentionResult.setComment(comment);
    return mentionResult;
  }
  
  public void notifyOrder(Order order, long videoId) {
    videoRepository.findById(videoId)
        .ifPresent(video -> {
          Notification notification = new Notification(order, video);
          log.debug("notification: {}", notification);
  
          notificationRepository.save(notification);
          deviceService.push(notification);
        });
  }

  public void notifyMemberPoint(MemberPoint memberPoint) {
    Member admin = memberService.currentMember();

    Notification notification = notificationRepository.save(new Notification(memberPoint, admin, "point"));
    log.info("notification: {}", notification);

    deviceService.push(notification);
  }

  public void notifyDeductMemberPoint(MemberPoint memberPoint) {
    Member admin = memberService.getAdmin();
    Notification notification = notificationRepository.save(new Notification(memberPoint, admin, "deduct_point"));
    log.info("notification: {}", notification);

    deviceService.push(notification);
  }

  public void notifyReminderMemberPoint(MemberPoint memberPoint) {
    Member admin = memberService.getAdmin();
    Notification notification = notificationRepository.save(new Notification(memberPoint, admin, "remind_point"));
    log.info("notification: {}", notification);

    deviceService.push(notification);
  }

  public void notifyWelcomeCoupon(MemberCoupon memberCoupon) {
    Notification notification = notificationRepository.save(new Notification(memberCoupon));
    log.info("notification: {}", notification);

    deviceService.push(notification);
  }
}
