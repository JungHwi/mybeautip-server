package com.jocoos.mybeautip.notification;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.post.PostComment;
import com.jocoos.mybeautip.post.PostCommentRepository;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoComment;
import com.jocoos.mybeautip.video.VideoCommentRepository;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoRepository;

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
      .ifPresent(v ->
        memberRepository.findById(videoComment.getCreatedBy())
           .ifPresent(source -> {
             v.setThumbnailUrl(getVideoThumbnail(v.getVideoKey()));
             Notification n = notificationRepository.save(new Notification(v, videoComment, source));
             deviceService.push(n);
           })
      );
  }

  public void notifyAddVideoCommentReply(VideoComment videoComment) {
    Member parentMember = Optional.ofNullable(videoComment.getParentId())
      .flatMap(parent -> videoCommentRepository.findById(parent))
      .flatMap(comment -> memberRepository.findById(comment.getCreatedBy()))
      .get();

    videoRepository.findById(videoComment.getVideoId())
       .ifPresent(v ->
          memberRepository.findById(videoComment.getCreatedBy())
             .ifPresent(source -> {
               Notification n = notificationRepository.save(new Notification(getVideoThumbnail(v.getVideoKey()), videoComment, source, parentMember));
               deviceService.push(n);
             })
       );
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
    memberRepository.findById(videoLike.getCreatedBy())
      .ifPresent(source -> {
        Notification n = notificationRepository.save(new Notification(videoLike,
          getVideoThumbnail(videoLike.getVideo().getVideoKey()), source));
        deviceService.push(n);
      });
  }
}
