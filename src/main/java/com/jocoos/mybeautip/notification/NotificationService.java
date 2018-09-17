package com.jocoos.mybeautip.notification;

import java.util.Date;
import java.util.Optional;

import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.post.*;
import com.jocoos.mybeautip.video.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

  @Value("${flipflop.video.thumbnail-format}")
  private  String videoThumbnailFormat;

  private final MemberRepository memberRepository;
  private final VideoRepository videoRepository;
  private final VideoCommentRepository videoCommentRepository;
  private final PostRepository postRepository;
  private final PostCommentRepository postCommentRepository;
  private final FollowingRepository followingRepository;
  private final DeviceService deviceService;
  private final NotificationRepository notificationRepository;

  public NotificationService(MemberRepository memberRepository,
                             VideoRepository videoRepository,
                             VideoCommentRepository videoCommentRepository,
                             PostRepository postRepository,
                             PostCommentRepository postCommentRepository, FollowingRepository followingRepository,
                             DeviceService deviceService,
                             NotificationRepository notificationRepository) {
    this.memberRepository = memberRepository;
    this.videoRepository = videoRepository;
    this.videoCommentRepository = videoCommentRepository;
    this.postRepository = postRepository;
    this.postCommentRepository = postCommentRepository;
    this.followingRepository = followingRepository;
    this.deviceService = deviceService;
    this.notificationRepository = notificationRepository;
  }

  public void notifyCreateVideo(Video video) {
    Long creator = video.getMember().getId();
    followingRepository.findByCreatedAtBeforeAndMemberYouId(new Date(), creator)
       .forEach(f -> {
         Notification notification = new Notification(video, getVideoThumbnail(video.getVideoKey()),  f.getMemberMe());
         log.debug("notification: {}", notification);

         notificationRepository.save(notification);
         deviceService.push(notification);
       });
  }

  private String getVideoThumbnail(String videoKey) {
    return String.format(videoThumbnailFormat, videoKey);
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

  public void notifyAddVideoComment(VideoComment videoComment) {
    videoRepository.findById(videoComment.getVideoId())
      .ifPresent(v -> {
         v.setThumbnailUrl(getVideoThumbnail(v.getVideoKey()));
         Notification n = notificationRepository.save(new Notification(v, videoComment));
         deviceService.push(n);
      });
  }

  public void notifyAddVideoCommentReply(VideoComment videoComment) {
    Member parentMember = Optional.ofNullable(videoComment.getParentId())
      .flatMap(parent -> videoCommentRepository.findById(parent))
      .map(VideoComment::getCreatedBy)
      .orElse(null);

    videoRepository.findById(videoComment.getVideoId())
       .ifPresent(v -> {
         Notification n = notificationRepository.save(new Notification(getVideoThumbnail(v.getVideoKey()), videoComment, parentMember));
         deviceService.push(n);
      });
  }

  public void notifyAddPostComment(PostComment postComment) {
    postRepository.findById(postComment.getPostId())
       .ifPresent(post -> {
         Notification n = notificationRepository.save(new Notification(post, postComment));
         deviceService.push(n);
       });
  }

  public void notifyAddPostCommentReply(PostComment postComment) {
    Member parentMember = Optional.ofNullable(postComment.getParentId())
       .flatMap(parent -> postCommentRepository.findById(parent))
       .map(PostComment::getCreatedBy)
       .orElse(null);


    postRepository.findById(postComment.getPostId())
       .ifPresent(post -> {
         Notification n = notificationRepository.save(new Notification(post.getThumbnailUrl(), postComment, parentMember));
         deviceService.push(n);
       });
  }

  public void notifyAddVideoLike(VideoLike videoLike) {
    Notification n = notificationRepository.save(new Notification(videoLike,
      getVideoThumbnail(videoLike.getVideo().getVideoKey()), videoLike.getCreatedBy()));
    deviceService.push(n);
  }

  public void notifyAddPostCommentLike(PostCommentLike postCommentLike) {
    String thumbnail = postRepository.findById(postCommentLike.getComment().getPostId())
       .map(Post::getThumbnailUrl).orElseGet(null);

    Notification n = notificationRepository.save(new Notification(postCommentLike, thumbnail));
    deviceService.push(n);
  }

  public void notifyAddVideoCommentLike(VideoCommentLike videoCommentLike) {
    String thumbnail = videoRepository.findById(videoCommentLike.getComment().getVideoId())
       .map(Video::getThumbnailUrl).orElseGet(null);

    Notification n = notificationRepository.save(new Notification(videoCommentLike, thumbnail));
    deviceService.push(n);
  }
}
