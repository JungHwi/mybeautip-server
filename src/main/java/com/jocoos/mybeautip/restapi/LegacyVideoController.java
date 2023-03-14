package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.comment.CommentReportInfo;
import com.jocoos.mybeautip.comment.SimpleCommentReportInfo;
import com.jocoos.mybeautip.comment.CreateCommentRequest;
import com.jocoos.mybeautip.comment.UpdateCommentRequest;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.global.exception.*;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.goods.TimeSaleCondition;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.block.BlockService;
import com.jocoos.mybeautip.member.comment.*;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.revenue.*;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.search.KeywordService;
import com.jocoos.mybeautip.video.*;
import com.jocoos.mybeautip.video.report.VideoReportRepository;
import com.jocoos.mybeautip.video.scrap.LegacyVideoScrapService;
import com.jocoos.mybeautip.video.scrap.VideoScrap;
import com.jocoos.mybeautip.video.view.VideoView;
import com.jocoos.mybeautip.video.view.VideoViewRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.jocoos.mybeautip.global.code.LikeStatus.LIKE;
import static com.jocoos.mybeautip.member.comment.Comment.CommentState.DEFAULT;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LegacyVideoController {
    private static final String VIDEO_NOT_FOUND = "video.not_found";
    private static final String COMMENT_NOT_FOUND = "comment.not_found";
    private static final String COMMENT_ALREADY_REPORTED = "comment.already_reported";
    private static final String LIKE_NOT_FOUND = "like.not_found";
    private static final String SCRAP_NOT_FOUND = "scrap.not_found";
    private static final String ALREADY_LIKED = "like.already_liked";
    private static final String ALREADY_SCRAPED = "scrap.already_scraped";
    private static final String COMMENT_WRITE_NOT_ALLOWED = "comment.write_not_allowed";
    private static final String COMMENT_BLOCKED_MESSAGE = "comment.blocked_message";
    private static final String VIDEO_ALREADY_REPORTED = "video.already_reported";
    private static final String COMMENT_LOCKED = "comment.locked";
    private static final String HASHTAG_SIGN = "#";
    private final LegacyMemberService legacyMemberService;
    private final GoodsService goodsService;
    private final LegacyVideoService legacyVideoService;
    private final MessageService messageService;
    private final VideoRepository videoRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final VideoGoodsRepository videoGoodsRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final VideoReportRepository videoReportRepository;
    private final VideoViewRepository videoViewRepository;
    private final CommentService commentService;
    private final MentionService mentionService;
    private final RevenueService revenueService;
    private final KeywordService keywordService;
    private final RevenueRepository revenueRepository;
    private final LegacyVideoScrapService legacyVideoScrapService;
    private final CommentReportRepository commentReportRepository;
    private final BlockService blockService;

    private final ActivityPointService activityPointService;

    @Value("${mybeautip.video.watch-duration}")
    private long watchDuration;

    @PostMapping("/1/videos")
    public VideoInfo createVideo(@Valid @RequestBody CreateVideoRequest request) {
        log.info("createVideo: {}", request.toString());
        Video createdVideo = legacyVideoService.create(request);
        VideoInfo videoInfo = legacyVideoService.generateVideoInfo(createdVideo);
        return videoInfo;
    }

    @GetMapping("/1/videos/search")
    public CursorResponse searchVideos(@RequestParam(defaultValue = "50") int count,
                                       @RequestParam(required = false) String cursor,
                                       @RequestParam String keyword) {
        keyword = keyword.trim();

        Slice<Video> list;
        if (StringUtils.isNotEmpty(keyword) && keyword.startsWith(HASHTAG_SIGN)) {
            list = legacyVideoService.findVideosWithTag(keyword, cursor, count);
        } else {
            list = legacyVideoService.findVideosWithKeyword(keyword, cursor, count);
        }
        List<VideoInfo> videos = new ArrayList<>();
        list.stream().forEach(v -> videos.add(legacyVideoService.generateVideoInfo(v)));

        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.trim();

            try {
                keywordService.updateKeywordCount(keyword);
                keywordService.logHistory(keyword, KeywordService.KeywordCategory.VIDEO, legacyMemberService.currentMember());
            } catch (ConcurrencyFailureException e) { // Ignore
                log.warn("getVideos throws ConcurrencyFailureException: " + keyword);
            }
        }

        String nextCursor = null;
        if (videos.size() > 0) {
            nextCursor = String.valueOf(videos.get(videos.size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/videos/search", videos)
                .withKeyword(keyword)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @GetMapping("/1/videos/{id}/goods")
    public List<GoodsInfo> getRelatedGoods(@PathVariable("id") Long id,
                                           @RequestParam(name = "broker", required = false) Long broker) {
        List<VideoGoods> list = videoGoodsRepository.findAllByVideoId(id);

        List<GoodsInfo> relatedGoods = new ArrayList<>();
        for (VideoGoods video : list) {
            relatedGoods.add(goodsService.generateGoodsInfo(video.getGoods(), TimeSaleCondition.createWithBroker(broker)));
        }
        return relatedGoods;
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
                .Builder<>("/api/1/videos/" + id + "/comments", result)
                .withCount(count)
                .withCursor(nextCursor)
                .withTotalCount(totalCount).toBuild();
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
                    return new ResponseEntity<>(new CommentStateInfo(state), HttpStatus.OK);
                })
                .orElseThrow(() -> new NotFoundException("invalid video key or comment id"));
    }

    /**
     * Likes
     */
    @PostMapping("/1/videos/{videoId:.+}/likes")
    public ResponseEntity<VideoLikeInfo> addVideoLike(@PathVariable Long videoId,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        try {
            Video video = legacyVideoService.getByVideoId(videoId);
            VideoLike videoLike = legacyVideoService.likeVideo(video, member);
            VideoLikeInfo info = new VideoLikeInfo(videoLike, legacyVideoService.generateVideoInfo(video));
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang));
        } catch (BadRequestException e) {
            throw new BadRequestException(messageService.getMessage(ALREADY_LIKED, lang));
        }
    }

    @PatchMapping("/1/videos/{videoId:.+}/likes/{likeId:.+}")
    public ResponseEntity<?> removeVideoLike(@PathVariable Long videoId,
                                             @PathVariable Long likeId,
                                             @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Long memberId = legacyMemberService.currentMemberId();

        videoLikeRepository.findByIdAndVideoIdAndCreatedById(likeId, videoId, memberId)
                .map(liked -> {
                    legacyVideoService.unLikeVideo(liked);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(LIKE_NOT_FOUND, lang)));

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/1/videos/{videoId:.+}/likes/{likeId:.+}")
    public ResponseEntity<?> removeVideoLikeLegacy(@PathVariable Long videoId,
                                             @PathVariable Long likeId,
                                             @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Long memberId = legacyMemberService.currentMemberId();

        videoLikeRepository.findByIdAndVideoIdAndCreatedById(likeId, videoId, memberId)
                .map(liked -> {
                    legacyVideoService.unLikeVideo(liked);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(LIKE_NOT_FOUND, lang)));

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/1/videos/{id:.+}/likes")
    public CursorResponse getLikedMemberList(@PathVariable Long id,
                                             @RequestParam(defaultValue = "100") int count,
                                             @RequestParam(required = false) String cursor) {
        Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
        PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<VideoLike> slice = videoLikeRepository.findByVideoIdAndCreatedAtBeforeAndVideoDeletedAtIsNullAndStatus(id, startCursor, pageable, LIKE);
        List<MemberInfo> members = new ArrayList<>();
        slice.stream().forEach(view -> members.add(legacyMemberService.getMemberInfo(view.getCreatedBy())));

        String nextCursor = null;
        if (members.size() > 0) {
            nextCursor = String.valueOf(slice.getContent().get(slice.getContent().size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/videos/" + id + "/likes", members)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    /**
     * Comment Likes
     */
    @PostMapping("/1/videos/{videoId:.+}/comments/{commentId:.+}/likes")
    public ResponseEntity<CommentLikeInfo> addCommentLike(@PathVariable Long videoId,
                                                          @PathVariable Long commentId,
                                                          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();
        try {
            CommentLike commentLike = legacyVideoService.likeVideoComment(commentId, videoId, member);
            return new ResponseEntity<>(new CommentLikeInfo(commentLike), HttpStatus.OK);
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
     * Watches
     */
    @PostMapping("/1/videos/{id:.+}/watches")
    public ResponseEntity<VideoInfo> joinWatch(@PathVariable Long id,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));
        Member me = legacyMemberService.currentMember();

        try {
            if ("LIVE".equalsIgnoreCase(video.getState())) {
                if (me == null) { // Guest
                    video = legacyVideoService.setWatcherWithGuest(video, legacyMemberService.getGuestUserName());
                } else {
                    video = legacyVideoService.setWatcher(video, me);
                }
            }
        } catch (ConcurrencyFailureException e) { // Ignore
            log.warn("joinWatch throws ConcurrencyFailureException");
        }

        return new ResponseEntity<>(legacyVideoService.generateVideoInfo(video), HttpStatus.OK);
    }

    @PatchMapping("/1/videos/{id:.+}/watches")
    public ResponseEntity<VideoInfo> keepWatch(@PathVariable Long id,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {

        Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));
        Member me = legacyMemberService.currentMember();

        try {
            if ("LIVE".equalsIgnoreCase(video.getState())) {
                if (me == null) {
                    video = legacyVideoService.updateWatcherWithGuest(video, legacyMemberService.getGuestUserName());
                } else {
                    video = legacyVideoService.updateWatcher(video, me);
                }
            }
        } catch (ConcurrencyFailureException e) { // Ignore
            log.warn("keepWatch throws ConcurrencyFailureException");
        }

        return new ResponseEntity<>(legacyVideoService.generateVideoInfo(video), HttpStatus.OK);
    }

    @DeleteMapping("/1/videos/{id:.+}/watches")
    public ResponseEntity<?> leaveWatch(@PathVariable Long id,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        Member me = legacyMemberService.currentMember();

        try {
            if (me == null) { // Guest
                legacyVideoService.removeGuestWatcher(video, legacyMemberService.getGuestUserName());
            } else {
                legacyVideoService.removeWatcher(video, me);
            }
        } catch (ConcurrencyFailureException e) { // Ignore
            log.warn("leaveWatch throws ConcurrencyFailureException");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/1/videos/{id:.+}/watches")
    public CursorResponse getWatcherList(@PathVariable Long id,
                                         @RequestParam(defaultValue = "100") int count,
                                         @RequestParam(required = false) String cursor,
                                         @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        return createViewerList(id, count, cursor, lang);
    }

    /**
     * Using owner of the Video
     */
    @GetMapping("/1/videos/{id:.+}/sales")
    public ResponseEntity<RevenueOverview> getRevenueOverview(@PathVariable Long id) {
        Member member = legacyMemberService.currentMember();
        RevenueOverview overview = revenueService.getOverview(id, member);

        return new ResponseEntity<>(overview, HttpStatus.OK);
    }

    /**
     * Using owner of the Video
     */
    @GetMapping("/1/videos/{id:.+}/revenues")
    public CursorResponse getRevenues(@PathVariable Long id,
                                      @RequestParam(defaultValue = "20") int count,
                                      @RequestParam(required = false) String cursor,
                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {

        Long memberId = legacyMemberService.currentMemberId();
        Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        if (!memberId.equals(video.getMember().getId())) {
            throw new AccessDeniedException("Invalid member id");
        }

        PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.ASC, "createdAt"));
        Slice<Revenue> slice;

        if (StringUtils.isNumeric(cursor)) {
            Date createdAt = new Date(Long.parseLong(cursor));
            slice = revenueRepository.findByVideoAndCreatedAtBefore(video, createdAt, pageable);
        } else {
            slice = revenueRepository.findByVideo(video, pageable);
        }

        List<SalesInfo> revenues = new ArrayList<>();

        slice.getContent().forEach(r -> revenues.add(new SalesInfo(r)));

        String nextCursor = null;
        if (revenues.size() > 0) {
            nextCursor = String.valueOf(revenues.get(revenues.size() - 1).getCreatedAt());
        }

        return new CursorResponse.Builder<>("/api/1/videos/" + id + "/revenues", revenues)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    /**
     * Report
     */
    @PostMapping("/1/videos/{id:.+}/report")
    public ResponseEntity<VideoInfo> reportVideo(@PathVariable Long id,
                                                 @Valid @RequestBody LegacyVideoController.VideoReportRequest request,
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

    /**
     * Add Heart
     */
    @PostMapping("/1/videos/{id:.+}/hearts")
    public ResponseEntity<VideoInfo> heartVideo(@PathVariable Long id,
                                                @Valid @RequestBody(required = false) VideoHeartRequest request,
                                                BindingResult bindingResult,
                                                @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        // Guest can add heart_count
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getFieldError());
        }
        int count = 1;
        if (request != null && request.getCount() != null) {
            count = request.getCount();
        }

        Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));
        try {
            legacyVideoService.increaseHeart(video, count);
            video.setHeartCount(video.getHeartCount() + count);
        } catch (ConcurrencyFailureException e) { // Ignore
            log.warn("heartVideo throws ConcurrencyFailureException");
        }

        return new ResponseEntity<>(legacyVideoService.generateVideoInfo(video), HttpStatus.OK);
    }

    /**
     * Views
     */
    @PostMapping("/1/videos/{id:.+}/view_count")
    public ResponseEntity<VideoInfo> addView(@PathVariable Long id,
                                             @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Video video = videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        Member me = legacyMemberService.currentMember();
        if (me == null) {
            video = legacyVideoService.addViewWithGuest(video, legacyMemberService.getGuestUserName());
        } else {
            video = legacyVideoService.addView(video, me);
        }
        return new ResponseEntity<>(legacyVideoService.generateVideoInfo(video), HttpStatus.OK);
    }

    @GetMapping("/1/videos/{id:.+}/views")
    public CursorResponse getViewerList(@PathVariable Long id,
                                        @RequestParam(defaultValue = "100") int count,
                                        @RequestParam(required = false) String cursor,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        return createViewerList(id, count, cursor, lang);
    }

    /**
     * Scraps
     */
    @PostMapping("/1/videos/{videoId:.+}/scraps")
    public ResponseEntity<VideoScrapInfo> addVideoScrap(@PathVariable Long videoId,
                                                       @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();
        Video video = videoRepository.findByIdAndDeletedAtIsNull(videoId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        try {
            VideoScrap scrap = legacyVideoScrapService.scrapVideo(video, member);
            VideoScrapInfo info = new VideoScrapInfo(scrap, legacyVideoService.generateVideoInfo(scrap.getVideo()));
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (BadRequestException e) {
            throw new BadRequestException(messageService.getMessage(ALREADY_SCRAPED, lang));
        }
    }

    @PatchMapping("/1/videos/{videoId:.+}/scraps")
    public ResponseEntity<?> removeVideoScrap(@PathVariable Long videoId,
                                              @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        Video video = videoRepository.findByIdAndDeletedAtIsNull(videoId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        try {
            legacyVideoScrapService.deleteScrap(video, member);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(messageService.getMessage(SCRAP_NOT_FOUND, lang));
        }
    }

    @DeleteMapping("/1/videos/{videoId:.+}/scraps")
    public ResponseEntity<?> removeVideoScrapLegacy(@PathVariable Long videoId,
                                              @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        Video video = videoRepository.findByIdAndDeletedAtIsNull(videoId)
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        try {
            legacyVideoScrapService.deleteScrap(video, member);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(messageService.getMessage(SCRAP_NOT_FOUND, lang));
        }
    }

    /**
     * Comment Report
     */
    @PostMapping("/1/videos/{videoId:.+}/comments/{id:.+}/report")
    public ResponseEntity<CommentReportInfo> reportVideoComment(@PathVariable Long videoId,
                                                                @PathVariable Long id,
                                                                @Valid @RequestBody LegacyVideoController.CommentReportRequest request,
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
        return new ResponseEntity<>(new CommentReportInfo(report), HttpStatus.OK);
    }

    @PostMapping("/2/videos/{videoId:.+}/comments/{id:.+}/report")
    public ResponseEntity<SimpleCommentReportInfo> reportVideoComment2(@PathVariable Long videoId,
                                                                       @PathVariable Long id,
                                                                       @Valid @RequestBody LegacyVideoController.CommentReportRequest request,
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

    private CursorResponse createViewerList(Long id, int count, String cursor, String lang) {
        videoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
        PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "modifiedAt"));

        Slice<VideoView> slice = videoViewRepository.findByVideoIdAndAndCreatedByIsNotNullAndModifiedAtBefore(id, startCursor, pageable);
        List<MemberInfo> members = new ArrayList<>();
        slice.stream().forEach(view -> members.add(legacyMemberService.getMemberInfo(view.getCreatedBy())));

        String nextCursor = null;

        if (members.size() > 0) {
            nextCursor = String.valueOf(slice.getContent().get(slice.getContent().size() - 1).getModifiedAt().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/videos/" + id + "/views", members)
                .withCount(count)
                .withGuestCount(videoViewRepository.countByVideoIdAndCreatedByIsNull(id))
                .withCursor(nextCursor).toBuild();
    }

@Data
@NoArgsConstructor
public static class VideoInfo {
    private Long id;
    private String videoKey;
    private String type;
    private String state;
    private Boolean locked;
    private Boolean muted;
    private String visibility;
    private List<VideoCategoryResponse> category;
    private String title;
    private String content;
    private String url;
    private String originalFilename;
    private String thumbnailPath;
    private String thumbnailUrl;
    private String chatRoomId;
    private Integer duration;
    private String liveKey = "";
    private String outputType = "";
    private String data;
    private Integer watchCount;
    private Integer totalWatchCount;
    private Integer viewCount;
    private Integer heartCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer orderCount;
    private Long reportCount;
    private Integer relatedGoodsCount;
    private String relatedGoodsThumbnailUrl;
    private Long likeId;
    private Long scrapId;
    private MemberInfo owner;
    private Boolean blocked;
    private Date createdAt;
    private Date deletedAt;
    private String categoryNames;

    /**
     * Real watchers count that was collected for 10 seconds
     */
    private Integer realWatchCount;

    public VideoInfo(Video video, MemberInfo owner, Long likeId, Boolean blocked) {
        BeanUtils.copyProperties(video, this);
        this.owner = owner;
        this.likeId = likeId;
        this.blocked = blocked;
        if (this.relatedGoodsCount == null) {
            this.relatedGoodsCount = 0;
        }  // FIXME: check policy
        if (this.relatedGoodsThumbnailUrl == null) {
            this.relatedGoodsThumbnailUrl = "";
        } // FIXME: check policy
    }

    public static VideoResponse toVideoResponse(VideoInfo videoInfo) {
        VideoResponse videoResponse = new VideoResponse();
        BeanUtils.copyProperties(videoInfo, videoResponse);
        return videoResponse;
    }
}

@Data
public static class CreateVideoRequest {
    @NotNull
    String type = "BROADCASTED";
    String visibility = "PUBLIC";
    List<Integer> category;
    String title = "";
    String content = "";
    String chatRoomId = "";
    String data = "";
    Boolean muted = false;
    Boolean locked = false;
}

@Data
public static class VideoLikeInfo {
    private Long id;
    @Deprecated
    private Long createdBy;
    private Date createdAt;
    private VideoInfo video;

    public VideoLikeInfo(VideoLike videoLike, VideoInfo video) {
        BeanUtils.copyProperties(videoLike, this);
        this.video = video;
    }
}

@Data
public static class CommentLikeInfo {
    private Long id;
    @Deprecated
    private MemberInfo createdBy;
    private Date createdAt;
    private CommentInfo comment;

    public CommentLikeInfo(CommentLike commentLike) {
        BeanUtils.copyProperties(commentLike, this);
        comment = new CommentInfo(commentLike.getComment());
    }
}

@Data
private static class VideoReportRequest {
    @NotNull
    @Size(max = 80)
    private String reason;

    private Integer reasonCode;
}

@Data
private static class VideoHeartRequest {
    private Integer count;
}

@Data
public static class VideoScrapInfo {
    private Long id;
    @Deprecated
    private Long createdBy;
    private Date createdAt;
    private VideoResponse video;

    public VideoScrapInfo(VideoScrap VideoScrap, VideoInfo video) {
        BeanUtils.copyProperties(VideoScrap, this);
        this.video = VideoInfo.toVideoResponse(video);
        this.video.setCreatedAt(ZonedDateTimeUtil.toUTCZoned(video.getCreatedAt()));
    }
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
