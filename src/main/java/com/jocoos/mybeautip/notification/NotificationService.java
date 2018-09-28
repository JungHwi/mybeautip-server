package com.jocoos.mybeautip.notification;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@Service
public class NotificationService {
  private final VideoRepository videoRepository;
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final FollowingRepository followingRepository;
  private final DeviceService deviceService;
  private final NotificationRepository notificationRepository;

  public NotificationService(VideoRepository videoRepository,
                             CommentRepository commentRepository,
                             PostRepository postRepository,
                             FollowingRepository followingRepository,
                             DeviceService deviceService,
                             NotificationRepository notificationRepository) {
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.followingRepository = followingRepository;
    this.deviceService = deviceService;
    this.notificationRepository = notificationRepository;
  }

  public void notifyCreateVideo(Video video) {
    Long creator = video.getMember().getId();
    followingRepository.findByCreatedAtBeforeAndMemberYouId(new Date(), creator)
       .forEach(f -> {
         Notification notification = new Notification(video, video.getThumbnailUrl(),  f.getMemberMe());
         log.debug("notification: {}", notification);

         notificationRepository.save(notification);
         deviceService.push(notification);
       });
  }

  public void notifyFollowMember(Following following) {
    Notification notification = followingRepository.findByMemberMeIdAndMemberYouId(
       following.getMemberYou().getId(), following.getMemberMe().getId())
         .map(f -> new Notification(following, f.getId()))
         .orElseGet(() -> new Notification(following, null));

    Notification n = notificationRepository.save(notification);
    log.debug("notification: {}", n);
    deviceService.push(n);
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
           n = notificationRepository.save(new Notification(comment, comment.getParentId(), parent, post.getThumbnailUrl()));
         } else {
           if (!(comment.getCreatedBy().getId().equals(post.getCreatedBy().getId()))) {
             n = notificationRepository.save(new Notification(comment, post.getCreatedBy(), post.getThumbnailUrl()));
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
           n = notificationRepository.save(new Notification(comment, comment.getParentId(), parent, v.getThumbnailUrl()));
         } else {
           if (!(comment.getCreatedBy().getId().equals(v.getMember().getId()))) {
             n = notificationRepository.save(new Notification(comment, v.getMember(), v.getThumbnailUrl()));
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
      String thumbnail = null;
      if (commentLike.getComment().getVideoId() != null) {
        thumbnail = videoRepository.findById(commentLike.getComment().getVideoId())
           .map(Video::getThumbnailUrl).orElseGet(null);
      }

      if (commentLike.getComment().getPostId() != null) {
        thumbnail = postRepository.findById(commentLike.getComment().getPostId())
           .map(Post::getThumbnailUrl).orElseGet(null);
      }

      Notification n = notificationRepository.save(new Notification(commentLike, thumbnail));
      deviceService.push(n);
    }
  }

  public void notifyAddVideoLike(VideoLike videoLike) {
    if (!(videoLike.getCreatedBy().getId().equals(videoLike.getVideo().getMember().getId()))) {
      Notification n = notificationRepository.save(new Notification(videoLike, videoLike.getCreatedBy()));
      deviceService.push(n);
    }
  }

  public void notifyAddComment(Comment comment, Member... mentioned) {
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
             Notification n = notificationRepository.save(new Notification(post, comment, m));
             log.debug("mentioned post comment: {}", n);
             deviceService.push(n);
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
             Notification n = notificationRepository.save(new Notification(v, comment, m));
             log.debug("mentioned video comment: {}", n);
             deviceService.push(n);
           });
         } else {
           notifyAddVideoComment(comment);
         }
       });
  }
}
