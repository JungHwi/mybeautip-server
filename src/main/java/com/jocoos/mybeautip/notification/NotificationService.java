package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Slf4j
@Service
public class NotificationService {
  private final DeviceService deviceService;
  private final MentionService mentionService;
  private final VideoRepository videoRepository;
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final FollowingRepository followingRepository;
  private final NotificationRepository notificationRepository;
  
  @Value("${mybeautip.notification.duplicate-limit-duration}")
  private int duration;

  public NotificationService(DeviceService deviceService,
                             MentionService mentionService,
                             VideoRepository videoRepository,
                             CommentRepository commentRepository,
                             PostRepository postRepository,
                             FollowingRepository followingRepository,
                             NotificationRepository notificationRepository) {
    this.deviceService = deviceService;
    this.mentionService = mentionService;
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.followingRepository = followingRepository;
    this.notificationRepository = notificationRepository;
  }

  public void notifyCreateVideo(Video video) {
    Long creator = video.getMember().getId();
    followingRepository.findByCreatedAtBeforeAndMemberYouId(new Date(), creator)
       .forEach(f -> {
         Notification notification = new Notification(video, video.getThumbnailUrl(), f.getMemberMe());
         log.debug("notification: {}", notification);

         notificationRepository.save(notification);
         deviceService.push(notification);
       });
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
          deviceService.push(n);
        }
      }

      if (commentLike.getComment().getPostId() != null) {
        Post post = postRepository.findById(commentLike.getComment().getPostId())
            .orElseThrow(() -> new NotFoundException("post_not_found", "Post not found: " + commentLike.getComment().getPostId()));
  
        int count = notificationRepository.countByTypeAndTargetMemberAndResourceIdAndResourceOwnerAndCreatedAtAfter(
            Notification.COMMENT_LIKE,
            commentLike.getComment().getCreatedBy(),
            commentLike.getComment().getId(),
            commentLike.getCreatedBy(),
            new Date(System.currentTimeMillis() - duration));
  
        if (count == 0) {
          n = notificationRepository.save(new Notification(post, commentLike, commentLike.getComment().getComment()));
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
                   MentionResult mentionResult = mentionService.createMentionComment(original);
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
                   MentionResult mentionResult = mentionService.createMentionComment(original);
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
}
