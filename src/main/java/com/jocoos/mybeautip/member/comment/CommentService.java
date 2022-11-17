package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.comment.CreateCommentRequest;
import com.jocoos.mybeautip.comment.UpdateCommentRequest;
import com.jocoos.mybeautip.domain.member.service.dao.MemberActivityCountDao;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.notification.LegacyNotificationService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.CommentSearchCondition;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.DELETE_VIDEO_COMMENT;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_VIDEO_COMMENT;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainAndReceiver;
import static com.jocoos.mybeautip.global.code.LikeStatus.LIKE;
import static com.jocoos.mybeautip.member.comment.Comment.CommentState.DELETED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    public static final int COMMENT_TYPE_VIDEO = 1;
    public static final int COMMENT_TYPE_POST = 2;
    private static final String COMMENT_LOCK_MESSAGE = "comment.lock_message";
    private static final String COMMENT_BLIND_MESSAGE = "comment.blind_message";
    private static final String COMMENT_BLIND_MESSAGE_BY_ADMIN = "comment.blind_message_by_admin";

    private final LegacyMemberService legacyMemberService;
    private final TagService tagService;
    private final MessageService messageService;
    private final MentionService mentionService;
    private final LegacyNotificationService legacyNotificationService;
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final CommentReportRepository commentReportRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberActivityCountDao activityCountDao;
    private final ActivityPointService activityPointService;

    @Transactional
    public List<CommentInfo> getComments(CommentSearchCondition condition, Pageable pageable) {
        List<Comment> comments = commentRepository.getComments(condition, pageable);

        List<CommentInfo> result = new ArrayList<>();
        comments.stream().forEach(comment -> {
            CommentInfo commentInfo = null;
            if (comment.getComment().contains("@")) {
                MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
                if (mentionResult != null) {
                    String content = this.getBlindContent(comment, condition.lang(), mentionResult.getComment());
                    comment.setComment(content);
                    commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()), mentionResult.getMentionInfo());
                } else {
                    log.warn("mention result not found - {}", comment);
                }
            } else {
                String content = getBlindContent(comment, condition.lang(), null);
                comment.setComment(content);
                commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()));
            }

            if (condition.memberId() != null) {
                Long likeId = commentLikeRepository.findByCommentIdAndCreatedByIdAndStatus(comment.getId(), condition.memberId(), LIKE)
                        .map(CommentLike::getId).orElse(null);
                commentInfo.setLikeId(likeId);
            }

            result.add(commentInfo);
        });

        return result;
    }

    @Transactional
    public void lockComment(Comment comment) {
        comment.setLocked(true);
        comment.setOriginalComment(comment.getComment());
        comment.setComment(messageService.getMessage(COMMENT_LOCK_MESSAGE, Locale.KOREAN));
        commentRepository.save(comment);
    }

    @Transactional
    public Comment addComment(CreateCommentRequest request, int type, long id, Member member) {
        if (request.getParentId() != null) {
            commentRepository.findById(request.getParentId())
                    .ifPresent(parent -> commentRepository.updateCommentCount(parent.getId(), 1));
        }

        Comment comment = new Comment();
        if (type == COMMENT_TYPE_VIDEO) {
            comment.setVideoId(id);
            videoRepository.updateCommentCount(id, 1);
        }
//        if (type == COMMENT_TYPE_POST) {
//            comment.setPostId(id);
//            postRepository.updateCommentCount(id, 1);
//        }

        BeanUtils.copyProperties(request, comment);
        comment = commentRepository.save(comment);

        tagService.touchRefCount(comment.getComment());
        tagService.addHistory(comment.getComment(), TagService.TAG_COMMENT, comment.getId(), comment.getCreatedBy());

        List<MentionTag> mentionTags = request.getMentionTags();
        if (mentionTags != null && mentionTags.size() > 0) {
            mentionService.updateCommentWithMention(comment, mentionTags);
        } else {
            legacyNotificationService.notifyAddComment(comment);
        }

        activityPointService.gainActivityPoint(WRITE_VIDEO_COMMENT, validDomainAndReceiver(comment, comment.getId(), member));
        activityCountDao.updateVideoCommentCount(member.getId(), 1);
        return comment;
    }

    @Transactional
    public Comment updateComment(UpdateCommentRequest request, Comment comment) {
        tagService.touchRefCount(request.getComment());
        tagService.updateHistory(comment.getComment(), request.getComment(), TagService.TAG_COMMENT, comment.getId(), comment.getCreatedBy());

        comment.setComment(request.getComment());
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentReport reportComment(Comment comment, Member me, int reasonCode, String reason) {
        CommentReport report = commentReportRepository.save(new CommentReport(comment, me, reasonCode, reason));
        comment.setReportCount(comment.getReportCount() + 1);
        commentRepository.save(comment);
        return report;
    }

    public String getBlindContent(Comment comment, String lang, String defaultValue) {
        String content;
        switch (comment.getCommentState()) {
            case BLINDED_BY_ADMIN:
                content = messageService.getMessage(COMMENT_BLIND_MESSAGE_BY_ADMIN, lang);
                break;
            case BLINDED:
                content = messageService.getMessage(COMMENT_BLIND_MESSAGE, lang);
                break;
            default:
                content = !StringUtils.isBlank(defaultValue) ? defaultValue : comment.getComment();
        }
        return content;
    }

    @Transactional
    public int deleteComment(Comment comment) {

        int childCount = commentRepository.countByParentIdAndCreatedByIdNot(comment.getId(), comment.getCreatedBy().getId());
        log.debug("child count: {}", childCount);

        activityPointService.retrieveActivityPoint(DELETE_VIDEO_COMMENT,
                comment.getId(), comment.getCreatedBy());
        activityCountDao.updateVideoCommentCount(comment.getCreatedBy().getId(), -1);

        return deleteCommentAndChildren(comment);
    }

    @Transactional
    public int deleteCommentAndChildren(Comment comment) {

        tagService.removeHistory(comment.getComment(), TagService.TAG_COMMENT, comment.getId(), comment.getCreatedBy());
        comment.setState(DELETED);

        deleteChildComments(comment);
        updateParentCommentChildCount(comment);
        deleteCommentLikes(comment);

        commentRepository.save(comment);
        return DELETED.value();
    }

    private void deleteCommentLikes(Comment comment) {
        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentId(comment.getId());
        commentLikeRepository.deleteAll(commentLikes);
    }

    private void updateParentCommentChildCount(Comment comment) {
        if (comment.getParentId() != null) {
            commentRepository.findById(comment.getParentId()).ifPresent(parentComment -> {
                if (parentComment.getCommentCount() > 0) {
                    commentRepository.updateCommentCount(parentComment.getId(), -1);
                }
            });
        }
    }

    private void deleteChildComments(Comment comment) {
        List<Long> ids = commentRepository.findByParentId(comment.getId())
                .stream()
                .map(Comment::getId)
                .toList();
        if (!CollectionUtils.isEmpty(ids)) {
            commentRepository.updateState(ids, DELETED.value());
        }

        final int count = 1 + ids.size();
        if (comment.getVideoId() != null) {
            log.info("deleted comment count for video: {}", count);
            videoRepository.findById(comment.getVideoId()).ifPresent(video -> {
                if (video.getCommentCount() > 0) {
                    videoRepository.updateCommentCount(video.getId(), count);
                }
            });
        }
    }
}
