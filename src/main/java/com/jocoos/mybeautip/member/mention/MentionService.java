package com.jocoos.mybeautip.member.mention;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.post.PostComment;
import com.jocoos.mybeautip.video.VideoComment;

@Slf4j
@Service
public class MentionService {

  private final MemberRepository memberRepository;
  private final NotificationService notificationService;

  public MentionService(MemberRepository memberRepository,
                        NotificationService notificationService) {
    this.memberRepository = memberRepository;
    this.notificationService = notificationService;
  }

  @Async
  public Comment getCommentWithMention(Comment postComment, MentionTagsRequest mentionTags) {
    if (mentionTags != null && mentionTags.size() > 0) {
      return postComment;
    }

    String comment = postComment.getComment();
    log.debug("post comment originals: {}", comment);

//    String mentionedComment = notificationMention(comment, mentionTags);
    for (MentionTagsRequest.MentionTag tag : mentionTags) {
      log.debug("comment: {}", comment);

      List<String> mentions = findMentionTags(comment);
      for (String mentioned : mentions) {
        if (mentioned.equals(tag.getUsername())) {
          memberRepository.findById(tag.getMemberId()).ifPresent(
             m -> {
               // TODO: Move to notificationAspect
               notificationService.notifyAddPostComment(postComment, m);

               comment.replaceAll(createMentionTag(tag.getUsername()), createMentionTag(tag.getMemberId()));
             }
          );
        }
      }
    }

    log.debug("post comment with mention: {}", comment);
    postComment.setComment(comment);

    return postComment;
  }

  @Async
  public Comment getCommentWithMention(Comment videoComment, MentionTagsRequest mentionTags) {
    if (mentionTags != null && mentionTags.size() > 0) {
      return videoComment;
    }

    String comment = videoComment.getComment();
    log.debug("video comment originals: {}", comment);

//    String mentionedComment = notificationMention(comment, mentionTags);
    for (MentionTagsRequest.MentionTag tag : mentionTags) {
      log.debug("comment: {}", comment);

      List<String> mentions = findMentionTags(comment);
      for (String mentioned : mentions) {
        if (mentioned.equals(tag.getUsername())) {
          memberRepository.findById(tag.getMemberId()).ifPresent(
             m -> {
               // TODO: Move to notificationAspect
               notificationService.notifyAddVideoComment(videoComment, m);

               comment.replaceAll(createMentionTag(tag.getUsername()), createMentionTag(tag.getMemberId()));
             }
          );
        }
      }
    }

    log.debug("video comment with mention: {}", comment);

    videoComment.setComment(comment);
    return videoComment;
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

  public static void main(String[] args) {
    String comment = "@tekhun @eunyoung_kim @tekhun @eunyoung_kim 안녕하세요";
    // U+AC00-U+D7A3
    String[] split = comment.split(" ");
    System.out.println(split.length);
    List<String> mentions = Arrays.stream(split)
       .filter(s -> s.startsWith("@"))
       .map(s -> s.substring(1))
       .collect(Collectors.toList());
    for (String s : mentions) {
      System.out.println(s);
    }


    String pattern = "@[:word:]";
    Pattern p = Pattern.compile(pattern);
    Matcher matcher = p.matcher(comment);
    matcher.find();
  }
}
