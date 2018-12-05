package com.jocoos.mybeautip.member.mention;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MentionService {

  private static final String MENTION_TAG = "@";
  
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
            comment = comment.replaceAll(createMentionTag(tag.getUsername()), createMentionTag(tag.getMemberId()));
            postComment.setComment(comment);
            notificationService.notifyAddCommentWithMention(postComment, member.get());
            log.debug("mentioned comment: {}", comment);
          }
        }
      }
    }

    log.debug("post comment with mention: {}", comment);
    postComment.setComment(comment);

    commentRepository.save(postComment);
  }

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
            comment = comment.replaceAll(createMentionTag(tag.getUsername()), createMentionTag(tag.getMemberId()));
            videoComment.setComment(comment);
            notificationService.notifyAddCommentWithMention(videoComment, member.get());
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
    StringBuilder sb = new StringBuilder(MENTION_TAG);
    return sb.append(username).toString();
  }

  private List<String> findMentionTags(String comment) {
    return Arrays.stream(comment.split(" "))
       .filter(c -> c.startsWith(MENTION_TAG))
       .map(c -> c.substring(1))
       .collect(Collectors.toList());
  }

  public MentionResult createMentionComment(String original) {
    MentionResult mentionResult = new MentionResult();

    String comment = original;
    List<String> mentions = findMentionTags(original);
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
    }

    log.debug("original comment: {}", comment);
    mentionResult.setComment(comment);
    return mentionResult;
  }
}
