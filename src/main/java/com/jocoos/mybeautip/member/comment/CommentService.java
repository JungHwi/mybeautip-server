package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.comment.CreateCommentRequest;
import com.jocoos.mybeautip.comment.UpdateCommentRequest;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.notification.LegacyNotificationService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_VIDEO_COMMENT;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainAndReceiver;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    public static final int COMMENT_TYPE_VIDEO = 1;
    public static final int COMMENT_TYPE_POST = 2;
    private static final String COMMENT_LOCK_MESSAGE = "comment.lock_message";
    private static final String COMMENT_BLIND_MESSAGE = "comment.blind_message";
    private static final String COMMENT_BLIND_MESSAGE_BY_ADMIN = "comment.blind_message_by_admin";
    private final TagService tagService;
    private final MessageService messageService;
    private final MentionService mentionService;
    private final LegacyNotificationService legacyNotificationService;
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final PostRepository postRepository;
    private final CommentReportRepository commentReportRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final ActivityPointService activityPointService;

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
        if (type == COMMENT_TYPE_POST) {
            comment.setPostId(id);
            postRepository.updateCommentCount(id, 1);
        }

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

        activityPointService.gainActivityPoint(WRITE_VIDEO_COMMENT, validDomainAndReceiver(comment, member));
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
        if (comment.getParentId() != null) {
            return deleteCommentAndChildren(comment);
        }

        int childCount = commentRepository.countByParentIdAndCreatedByIdNot(comment.getId(), comment.getCreatedBy().getId());
        log.debug("child count: {}", childCount);

        activityPointService.retrieveActivityPoint(WRITE_VIDEO_COMMENT,
                                                   comment.getId(), comment.getCreatedBy());

        if (childCount == 0) {
            return deleteCommentAndChildren(comment);
        } else {
            return blindComment(comment);
        }
    }

    @Transactional
    public int blindComment(Comment comment) {
        Comment.CommentState state = Comment.CommentState.BLINDED;
        comment.setState(state);
        commentRepository.save(comment);
        return state.value();
    }

    @Transactional
    public int deleteCommentAndChildren(Comment comment) {
        tagService.removeHistory(comment.getComment(), TagService.TAG_COMMENT, comment.getId(), comment.getCreatedBy());

        int count = 1;
        if (comment.getParentId() != null) {
            commentRepository.findById(comment.getParentId()).ifPresent(parentComment -> {
                if (parentComment.getCommentCount() > 0) {
                    commentRepository.updateCommentCount(parentComment.getId(), -1);
                }
            });
        } else {
            if (comment.getCommentCount() > 0) {
                count += commentRepository.deleteByParentIdAndCreatedById(comment.getId(), comment.getCreatedBy().getId());
            }
        }

        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentId(comment.getId());
        commentLikeRepository.deleteAll(commentLikes);
        commentRepository.delete(comment);

        if (comment.getVideoId() != null) {
            final int commentCount = -count;
            log.info("deleted comment count for video: {}", count);
            videoRepository.findById(comment.getVideoId()).ifPresent(video -> {
                if (video.getCommentCount() > 0) {
                    videoRepository.updateCommentCount(video.getId(), commentCount);
                }
            });
        }

        if (comment.getPostId() != null) {
            final int commentCount = -count;
            log.info("deleted comment count for post: {}", count);
            postRepository.findById(comment.getPostId()).ifPresent(post -> {
                if (post.getCommentCount() > 0) {
                    postRepository.updateCommentCount(post.getId(), commentCount);
                }
            });
        }

        return Comment.CommentState.DELETED.value();
    }
}
