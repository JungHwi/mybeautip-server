package com.jocoos.mybeautip.member.mention;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.notification.LegacyNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MentionService {

    private static final String MENTION_TAG = "@";

    private final MemberRepository memberRepository;
    private final LegacyNotificationService legacyNotificationService;
    private final CommentRepository commentRepository;

    public MentionService(MemberRepository memberRepository,
                          LegacyNotificationService legacyNotificationService,
                          CommentRepository commentRepository) {
        this.memberRepository = memberRepository;
        this.legacyNotificationService = legacyNotificationService;
        this.commentRepository = commentRepository;
    }

    public void updateCommentWithMention(Comment comment, List<MentionTag> mentionTags) {
        if (mentionTags == null || mentionTags.size() == 0) {
            return;
        }

        // FIXME: Uncheck my username and username in following list
        Map<String, Long> mentionTagMap = new HashMap<>();
        for (MentionTag tag : mentionTags) {
            mentionTagMap.put(tag.getUsername(), tag.getMemberId());
        }

        Set<Member> notifyTargetMember = new HashSet<>();

        String commentMessage = comment.getComment().trim();
        log.debug("comment originals: {}", commentMessage);
        StringBuilder sb = new StringBuilder();

        String target = commentMessage;
        StringTokenizer tokenizer;
        do {
            tokenizer = new StringTokenizer(target);
            if (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.startsWith(MENTION_TAG)) {
                    String username = token.substring(MENTION_TAG.length());
                    if (mentionTagMap.containsKey(username)) {
                        Member member = memberRepository.findById(mentionTagMap.get(username)).orElse(null);
                        if (member != null) {
                            sb.append(MENTION_TAG).append(mentionTagMap.get(username)).append(" ");
                            notifyTargetMember.add(member);
                        } else {
                            sb.append(token).append(" ");
                        }
                    } else {
                        sb.append(token).append(" ");
                    }
                } else {
                    sb.append(token).append(" ");
                }
                target = StringUtils.substringAfter(target, token);
            }
        } while (target.length() > 0);

        for (Member member : notifyTargetMember) {
            legacyNotificationService.notifyAddCommentWithMention(comment, member);
        }

        log.debug("comment with mention: {}", sb.toString().trim());
        comment.setComment(sb.toString().trim());

        commentRepository.save(comment);
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
