package com.jocoos.mybeautip.notification;

import java.util.Date;
import java.util.Optional;

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
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@Service
public class NotificationService {

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
         Notification n = notificationRepository.save(new Notification(video, f.getMemberMe()));
         deviceService.push(n);
       });
  }

  public void notifyFollowMember(Following following) {
    Notification n = notificationRepository.save(new Notification(following));
    deviceService.push(n);
  }


  public void notifyAddVideoComment(VideoComment videoComment) {
    videoRepository.findById(videoComment.getVideoId())
      .ifPresent(v ->
        memberRepository.findById(videoComment.getCreatedBy())
           .ifPresent(m -> {
             Notification n = notificationRepository.save(new Notification(v, videoComment, m));
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
             .ifPresent(m -> {
               Notification n = notificationRepository.save(new Notification(v, videoComment, m, parentMember));
               deviceService.push(n);
             })
       );
  }

  public void notifyAddPostComment(PostComment postComment) {
    Member source = memberRepository.findById(postComment.getCreatedBy()).get();

    postRepository.findById(postComment.getPostId())
       .ifPresent(post ->
          memberRepository.findById(postComment.getCreatedBy())
             .ifPresent(m -> {
               Notification n = notificationRepository.save(new Notification(Notification.POST_COMMENT, post, postComment, source, m));
               deviceService.push(n);
             })
       );
  }

  public void notifyAddPostCommentReply(PostComment postComment) {
    Member parentMember = Optional.ofNullable(postComment.getParentId())
       .flatMap(parent -> videoCommentRepository.findById(parent))
       .flatMap(comment -> memberRepository.findById(comment.getCreatedBy()))
       .get();

    postRepository.findById(postComment.getPostId())
       .ifPresent(post ->
          memberRepository.findById(postComment.getCreatedBy())
             .ifPresent(m -> {
               Notification n = notificationRepository.save(new Notification(Notification.POST_COMMENT_REPLY, post, postComment, m, parentMember));
               deviceService.push(n);
             })
       );
  }

}
