package com.jocoos.mybeautip.domain.video.api.internal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.jocoos.mybeautip.comment.CreateCommentRequest;
import com.jocoos.mybeautip.comment.SimpleCommentReportInfo;
import com.jocoos.mybeautip.comment.UpdateCommentRequest;
import com.jocoos.mybeautip.global.exception.*;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.*;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.CommentSearchCondition;
import com.jocoos.mybeautip.restapi.CursorResponse;
import com.jocoos.mybeautip.restapi.LegacyVideoController;
import com.jocoos.mybeautip.video.*;
import com.jocoos.mybeautip.video.report.VideoReportRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static com.jocoos.mybeautip.member.comment.Comment.CommentState.DEFAULT;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(value = "/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class InternalLegacyVideoController {
    private static final String VIDEO_NOT_FOUND = "video.not_found";
    private static final String COMMENT_NOT_FOUND = "comment.not_found";
    private static final String COMMENT_ALREADY_REPORTED = "comment.already_reported";
    private static final String LIKE_NOT_FOUND = "like.not_found";
    private static final String ALREADY_LIKED = "like.already_liked";
    private static final String COMMENT_WRITE_NOT_ALLOWED = "comment.write_not_allowed";
    private static final String VIDEO_ALREADY_REPORTED = "video.already_reported";
    private static final String COMMENT_LOCKED = "comment.locked";
    private final LegacyMemberService legacyMemberService;
    private final LegacyVideoService legacyVideoService;
    private final MessageService messageService;
    private final VideoRepository videoRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final VideoReportRepository videoReportRepository;
    private final CommentService commentService;
    private final CommentReportRepository commentReportRepository;

    /**
     * Likes
     */
    @PostMapping("/1/videos/{videoId:.+}/likes")
    public ResponseEntity<LegacyVideoController.VideoLikeInfo> addVideoLike(@PathVariable Long videoId,
                                                                            @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        try {
            Video video = legacyVideoService.getByVideoId(videoId);
            VideoLike videoLike = legacyVideoService.likeVideo(video, member);
            LegacyVideoController.VideoLikeInfo info = new LegacyVideoController.VideoLikeInfo(videoLike, legacyVideoService.generateVideoInfo(video));
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang));
        } catch (BadRequestException e) {
            throw new BadRequestException(messageService.getMessage(ALREADY_LIKED, lang));
        }
    }

    @DeleteMapping("/1/videos/{videoId:.+}/likes/{likeId:.+}")
    public ResponseEntity<?> removeVideoLikeLegacy(@PathVariable Long videoId,
                                             @PathVariable Long likeId,
                                             @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        videoLikeRepository.findByIdAndVideoIdAndCreatedById(likeId, videoId, member.getId())
                .map(liked -> {
                    legacyVideoService.unLikeVideo(liked);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(LIKE_NOT_FOUND, lang)));

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Comment Likes
     */
    @PostMapping("/1/videos/{videoId:.+}/comments/{commentId:.+}/likes")
    public ResponseEntity<LegacyVideoController.CommentLikeInfo> addCommentLike(@PathVariable Long videoId,
                                                                                @PathVariable Long commentId,
                                                                                @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();
        try {
            CommentLike commentLike = legacyVideoService.likeVideoComment(commentId, videoId, member);
            return new ResponseEntity<>(new LegacyVideoController.CommentLikeInfo(commentLike), HttpStatus.OK);
        } catch (BadRequestException e) {
            throw new BadRequestException(messageService.getMessage(ALREADY_LIKED, lang));
        }
    }

    @PatchMapping("/1/videos/{videoId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
    public ResponseEntity<?> removeCommentLike(@PathVariable Long videoId,
                                               @PathVariable Long commentId,
                                               @PathVariable Long likeId,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member me = legacyMemberService.currentMember();

        Comment comment = commentRepository.findByIdAndVideoId(commentId, videoId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND, messageService.getMessage(COMMENT_NOT_FOUND, lang)));

        commentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, comment.getId(), me.getId())
                .map(liked -> {
                    legacyVideoService.unLikeVideoComment(liked);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(LIKE_NOT_FOUND, lang)));
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/1/videos/{videoId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
    public ResponseEntity<?> removeCommentLikeLegacy(@PathVariable Long videoId,
                                               @PathVariable Long commentId,
                                               @PathVariable Long likeId,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member me = legacyMemberService.currentMember();

        Comment comment = commentRepository.findByIdAndVideoId(commentId, videoId)
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(COMMENT_NOT_FOUND, lang)));

        commentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, comment.getId(), me.getId())
                .map(liked -> {
                    legacyVideoService.unLikeVideoComment(liked);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(LIKE_NOT_FOUND, lang)));
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Report
     */
    @PostMapping("/1/videos/{id:.+}/report")
    public ResponseEntity<LegacyVideoController.VideoInfo> reportVideo(@PathVariable Long id,
                                                                       @Valid @RequestBody InternalLegacyVideoController.VideoReportRequest request,
                                                                       @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        int reasonCode = (request.getReasonCode() == null ? 0 : request.getReasonCode());
        Member me = legacyMemberService.currentMember();

        Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        if (videoReportRepository.findByVideoIdAndCreatedById(id, me.getId()).isPresent()) {
            throw new BadRequestException(messageService.getMessage(VIDEO_ALREADY_REPORTED, lang));
        }

        Video result = legacyVideoService.reportVideo(video, me, reasonCode, request.getReason());
        return new ResponseEntity<>(legacyVideoService.generateVideoInfo(result), HttpStatus.OK);
    }

    @PostMapping("/1/videos/{id:.+}/comments")
    public ResponseEntity addComment(@PathVariable Long id,
                                     @RequestBody CreateCommentRequest request,
                                     BindingResult bindingResult,
                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        if (bindingResult != null && bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getFieldError());
        }

        Member member = legacyMemberService.currentMember();

        if (!legacyMemberService.hasCommentPostPermission(member)) {
            throw new AccessDeniedException(messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
        }

        videoRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        if (request.getParentId() != null) {
            commentRepository.findById(request.getParentId())
                .map(c -> {
                    /**
                     * Not allow 2 depth comment in child comment
                     */
                    if (c.getParentId() != null) {
                        log.warn("comment is child comment: {}", c);
                        throw new BadRequestException(ErrorCode.COMMENT_NOT_FOUND, messageService.getMessage(COMMENT_NOT_FOUND, lang));
                    }
                    return c;
                })
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND, messageService.getMessage(COMMENT_NOT_FOUND, lang)));

        }

        Comment comment = commentService.addComment(request, CommentService.COMMENT_TYPE_VIDEO, id, member);
        return new ResponseEntity<>(new CommentInfo(comment), HttpStatus.OK);
    }

    @GetMapping("/1/videos/{id}/comments")
    public CursorResponse getComments(@PathVariable Long id,
                                      @RequestParam(defaultValue = "20") int count,
                                      @RequestParam(required = false) Long cursor,
                                      @RequestParam(required = false) String direction,
                                      @RequestParam(name = "parent_id", required = false) Long parentId,
                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        PageRequest page;
        if ("next".equals(direction)) {
            page = PageRequest.of(0, count, Sort.by(Sort.Direction.ASC, "id"));
        } else {
            page = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id")); // default
        }

        Long memberId = legacyMemberService.currentMemberId();
        CommentSearchCondition condition = CommentSearchCondition.builder()
            .videoId(id)
            .state(DEFAULT)
            .cursor(cursor)
            .parentId(parentId)
            .memberId(memberId)
            .lang(lang)
            .build();

        List<CommentInfo> result = commentService.getComments(condition, page);

        String nextCursor = null;
        if (result.size() > 0) {
            if ("next".equals(direction)) {
                nextCursor = String.valueOf(result.get(result.size() - 1).getId() + 1);
            } else {
                nextCursor = String.valueOf(result.get(result.size() - 1).getId() - 1);
            }
        }

        int totalCount = videoRepository.findById(id)
            .map(v -> v.getCommentCount()).orElse(0);

        return new CursorResponse
            .Builder<>("/internal/1/videos/" + id + "/comments", result)
            .withCount(count)
            .withCursor(nextCursor)
            .withTotalCount(totalCount).toBuild();
    }

    @PostMapping("/2/videos/{id:.+}/comments")
    public ResponseEntity addComment2(@PathVariable Long id,
                                     @RequestBody CreateCommentRequest request,
                                     BindingResult bindingResult,
                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        if (bindingResult != null && bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getFieldError());
        }

        Member member = legacyMemberService.currentMember();
        if (!legacyMemberService.hasCommentPostPermission(member)) {
            throw new AccessDeniedException(messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
        }

        videoRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        if (request.getParentId() != null) {
            commentRepository.findById(request.getParentId())
                .map(c -> {
                    /**
                     * Not allow 2 depth comment in child comment
                     */
                    if (c.getParentId() != null) {
                        log.warn("comment is child comment: {}", c);
                        throw new BadRequestException(ErrorCode.COMMENT_NOT_FOUND, messageService.getMessage(COMMENT_NOT_FOUND, lang));
                    }
                    return c;
                })
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND, messageService.getMessage(COMMENT_NOT_FOUND, lang)));

        }

        Comment comment = commentService.addComment(request, CommentService.COMMENT_TYPE_VIDEO, id, member);
        return new ResponseEntity<>(new CommentInfo(comment), HttpStatus.OK);
    }

    @PatchMapping("/1/videos/{videoId:.+}/comments/{id:.+}")
    public ResponseEntity updateComment(@PathVariable Long videoId,
                                        @PathVariable Long id,
                                        @RequestBody UpdateCommentRequest request,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
                                        BindingResult bindingResult) {

        if (bindingResult != null && bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getFieldError());
        }

        Member member = legacyMemberService.currentMember();
        if (!legacyMemberService.hasCommentPostPermission(member)) {
            throw new BadRequestException(messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
        }

        return commentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, member.getId())
            .map(comment -> {
                if (comment.getLocked()) {
                    throw new BadRequestException(messageService.getMessage(COMMENT_LOCKED, lang));
                }
                comment = commentService.updateComment(request, comment);
                return new ResponseEntity<>(new CommentInfo(comment), HttpStatus.OK);
            })
            .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND, "invalid video key id or comment id"));
    }

    @DeleteMapping("/1/videos/{videoId:.+}/comments/{id:.+}")
    public ResponseEntity<?> removeComment(@PathVariable Long videoId,
                                           @PathVariable Long id,
                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        return commentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, legacyMemberService.currentMemberId())
            .map(comment -> {
                if (comment.getLocked()) {
                    throw new BadRequestException(messageService.getMessage(COMMENT_LOCKED, lang));
                }

                int state = commentService.deleteComment(comment);
                return new ResponseEntity<>(new LegacyVideoController.CommentStateInfo(state), HttpStatus.OK);
            })
            .orElseThrow(() -> new NotFoundException("invalid video key or comment id"));
    }

    @PostMapping("/2/videos/{videoId:.+}/comments/{id:.+}/report")
    public ResponseEntity<SimpleCommentReportInfo> reportVideoComment2(@PathVariable Long videoId,
                                                                       @PathVariable Long id,
                                                                       @Valid @RequestBody InternalLegacyVideoController.CommentReportRequest request,
                                                                       @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        int reasonCode = (request.getReasonCode() == null ? 0 : request.getReasonCode());
        Member me = legacyMemberService.currentMember();

        Comment comment = commentRepository.findByIdAndVideoId(id, videoId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND, messageService.getMessage(COMMENT_NOT_FOUND, lang)));

        Optional<CommentReport> alreadyCommentReport = commentReportRepository.findByCommentIdAndCreatedById(id, me.getId());
        if (alreadyCommentReport.isPresent()) {
            throw new ConflictException(messageService.getMessage(COMMENT_ALREADY_REPORTED, lang));
        }

        CommentReport report = commentService.reportComment(comment, me, reasonCode, request.getReason());
        return new ResponseEntity<>(new SimpleCommentReportInfo(report), HttpStatus.OK);
    }

    @Data
    private static class VideoReportRequest {
        @NotNull
        @Size(max = 80)
        private String reason;

        private Integer reasonCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentStateInfo {
        private int state;
    }

    @Data
    private static class CommentReportRequest {
        @Size(max = 80)
        private String reason;

        @NotNull
        private Integer reasonCode;
    }
}
