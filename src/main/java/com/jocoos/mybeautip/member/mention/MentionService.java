package com.jocoos.mybeautip.member.mention;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.post.PostComment;
import com.jocoos.mybeautip.post.PostCommentRepository;
import com.jocoos.mybeautip.video.VideoComment;
import com.jocoos.mybeautip.video.VideoCommentRepository;

@Slf4j
@Service
public class MentionService {

  private final MemberRepository memberRepository;
  private final NotificationService notificationService;
  private final CommentRepository commentRepository;

  public MentionService(MemberRepository memberRepository,
                        NotificationService notificationService,
                        CommentRepository commentRepository) {
    this.memberRepository = memberRepository;
    this.notificationService = notificationService;
    this.commentRepository = commentRepository;
  }

  @Async
  public void updatePostCommentWithMention(Comment postComment, List<MentionTag> mentionTags) {
    if (mentionTags == null || mentionTags.size() == 0) {
      return;
    }

    String comment = postComment.getComment();
    log.debug("post comment originals: {}", comment);

    for (MentionTag tag : mentionTags) {
      log.debug("comment: {}", comment);

      List<String> mentions = findMentionTags(comment);
      for (String mentioned : mentions) {
        if (mentioned.equals(tag.getUsername())) {
          // FIXME: Uncheck my username and username in following list

          Optional<Member> member = memberRepository.findById(tag.getMemberId());
          if (member.isPresent()) {
            notificationService.notifyAddComment(postComment, member.get());
            comment = comment.replaceAll(createMentionTag(tag.getUsername()), createMentionTag(tag.getMemberId()));
            log.debug("mentioned comment: {}", comment);
          }
        }
      }
    }

    log.debug("post comment with mention: {}", comment);
    postComment.setComment(comment);

    commentRepository.save(postComment);
  }

  @Async
  public void updateVideoCommentWithMention(Comment videoComment, List<MentionTag> mentionTags) {
    if (mentionTags == null || mentionTags.size() == 0) {
      return;
    }

    String comment = videoComment.getComment();
    log.debug("video comment originals: {}", comment);

    for (MentionTag tag : mentionTags) {
      log.debug("comment: {}", comment);

      List<String> mentions = findMentionTags(comment);
      for (String mentioned : mentions) {
        if (mentioned.equals(tag.getUsername())) {
          // FIXME: Uncheck my username and username in following list

          Optional<Member> member = memberRepository.findById(tag.getMemberId());
          if (member.isPresent()) {
            notificationService.notifyAddVideoComment(videoComment, member.get());
            comment = comment.replaceAll(createMentionTag(tag.getUsername()), createMentionTag(tag.getMemberId()));
            log.debug("mentioned comment: {}", comment);
          }
        }
      }
    }

    log.debug("video comment with mention: {}", comment);
    videoComment.setComment(comment);

    commentRepository.save(videoComment);
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
