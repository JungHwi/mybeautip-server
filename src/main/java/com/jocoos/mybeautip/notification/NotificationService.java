package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

  public NotificationService(DeviceService deviceService,
                             VideoRepository videoRepository,
                             CommentRepository commentRepository,
                             PostRepository postRepository,
                             FollowingRepository followingRepository,
                             NotificationRepository notificationRepository,
                             MemberRepository memberRepository) {
    this.deviceService = deviceService;
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.followingRepository = followingRepository;
    this.notificationRepository = notificationRepository;
    this.memberRepository = memberRepository;
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
           n = notificationRepository.save(new Notification(post, comment, getCommentStr(comment), comment.getParentId(), parent, post.getThumbnailUrl()));
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
           n = notificationRepository.save(new Notification(v, comment, getCommentStr(comment), comment.getParentId(), parent, v.getThumbnailUrl()));
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
        n = notificationRepository.save(new Notification(video, commentLike, getCommentStr(commentLike.getComment())));
      }

      if (commentLike.getComment().getPostId() != null) {
        Post post = postRepository.findById(commentLike.getComment().getPostId())
            .orElseThrow(() -> new NotFoundException("post_not_found", "Post not found: " + commentLike.getComment().getPostId()));
        n = notificationRepository.save(new Notification(post, commentLike, getCommentStr(commentLike.getComment())));
      }

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
             if (!(comment.getCreatedBy().getId().equals(m.getId()))) {
               Notification n = notificationRepository.save(new Notification(post, comment, m));
               log.debug("mentioned post comment: {}", n);
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
  
  // Return comment with mention info
  private String getCommentStr(Comment comment) {
    String commentStr = comment.getComment();
    if (comment.getComment().contains("@")) {
      MentionResult mentionResult = new MentionResult();
  
      List<String> mentions = findMentionTags(commentStr);
      for (String memberId : mentions) {
        log.debug("member: {}", memberId);
    
        if (StringUtils.isNumeric(memberId)) {
          Optional<Member> member = memberRepository.findById(Long.parseLong(memberId));
          if (member.isPresent()) {
            Member m = member.get();
            mentionResult.add(new MentionTag(m));
            commentStr = commentStr.replaceAll(createMentionTag(m.getId()), createMentionTag(m.getUsername()));
          }
        }
      }
    }
    return commentStr;
  }
  
  private String createMentionTag(Object username) {
    StringBuilder sb = new StringBuilder("@");
    return sb.append(username).toString();
  }
  
  private List<String> findMentionTags(String comment) {
    return Arrays.stream(comment.split(" "))
        .filter(c -> c.startsWith("@"))
        .map(c -> c.substring(1))
        .collect(Collectors.toList());
  }
}
