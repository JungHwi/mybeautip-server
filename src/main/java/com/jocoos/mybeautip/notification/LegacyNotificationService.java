package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.recommendation.MemberRecommendationRepository;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LegacyNotificationService {
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

  private final TaskScheduler taskScheduler;

  public LegacyNotificationService(DeviceService deviceService,
                                   VideoRepository videoRepository,
                                   CommentRepository commentRepository,
                                   PostRepository postRepository,
                                   FollowingRepository followingRepository,
                                   NotificationRepository notificationRepository,
                                   MemberRepository memberRepository,
                                   MemberService memberService,
                                   MemberRecommendationRepository memberRecommendationRepository,
                                   InstantMessageService instantMessageService,
                                   TaskScheduler taskScheduler) {
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
    this.taskScheduler = taskScheduler;
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
            });
    }

  private Member findCommentMemberByParentId(Long parentId) {
    return commentRepository.findById(parentId)
            .map(Comment::getCreatedBy)
            .orElse(null);
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
        //deviceService.push(n);
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
//                    deviceService.push(n);
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
//                    deviceService.push(n);
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
}
