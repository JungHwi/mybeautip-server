//package com.jocoos.mybeautip.restapi;
//
//import com.jocoos.mybeautip.comment.CommentReportInfo;
//import com.jocoos.mybeautip.comment.CreateCommentRequest;
//import com.jocoos.mybeautip.comment.UpdateCommentRequest;
//import com.jocoos.mybeautip.global.exception.BadRequestException;
//import com.jocoos.mybeautip.global.exception.ConflictException;
//import com.jocoos.mybeautip.global.exception.NotFoundException;
//import com.jocoos.mybeautip.goods.GoodsInfo;
//import com.jocoos.mybeautip.goods.GoodsRepository;
//import com.jocoos.mybeautip.goods.GoodsService;
//import com.jocoos.mybeautip.member.LegacyMemberService;
//import com.jocoos.mybeautip.member.Member;
//import com.jocoos.mybeautip.member.MemberInfo;
//import com.jocoos.mybeautip.member.MemberRepository;
//import com.jocoos.mybeautip.member.block.Block;
//import com.jocoos.mybeautip.member.block.BlockService;
//import com.jocoos.mybeautip.member.comment.*;
//import com.jocoos.mybeautip.member.mention.MentionResult;
//import com.jocoos.mybeautip.member.mention.MentionService;
//import com.jocoos.mybeautip.notification.MessageService;
//import com.jocoos.mybeautip.post.*;
//import com.jocoos.mybeautip.search.KeywordService;
//import com.jocoos.mybeautip.support.AttachmentService;
//import com.jocoos.mybeautip.support.DateUtils;
//import com.jocoos.mybeautip.tag.TagService;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.dao.CannotAcquireLockException;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.validation.Valid;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.jocoos.mybeautip.global.code.LikeStatus.LIKE;
//
//@Slf4j
//@RestController
//@RequestMapping(path = "/api/1/posts", produces = MediaType.APPLICATION_JSON_VALUE)
//public class PostController {
//    private static final String COMMENT_NOT_FOUND = "comment.not_found";
//    private static final String LIKE_NOT_FOUND = "like.not_found";
//    private static final String POST_NOT_FOUND = "post.not_found";
//    private static final String ALREADY_LIKED = "like.already_liked";
//    private static final String COMMENT_WRITE_NOT_ALLOWED = "comment.write_not_allowed";
//    private static final String COMMENT_LOCKED = "comment.locked";
//    private static final String POST_ALREADY_REPORTED = "post.already_reported";
//    private static final String POST_BLOCKED_MESSAGE = "post.blocked_message";
//    private static final String COMMENT_BLOCKED_MESSAGE = "comment.blocked_message";
//    private static final String COMMENT_ALREADY_REPORTED = "comment.already_reported";
//    private final PostService postService;
//    private final PostRepository postRepository;
//    private final PostLikeRepository postLikeRepository;
//    private final CommentRepository commentRepository;
//    private final CommentLikeRepository commentLikeRepository;
//    private final GoodsService goodsService;
//    private final GoodsRepository goodsRepository;
//    private final LegacyMemberService legacyMemberService;
//    private final MemberRepository memberRepository;
//    private final CommentService commentService;
//    private final MentionService mentionService;
//    private final TagService tagService;
//    private final MessageService messageService;
//    private final KeywordService keywordService;
//    private final BlockService blockService;
//    private final AttachmentService attachmentService;
//    private final PostLabelRepository postLabelRepository;
//    private final PostReportRepository postReportRepository;
//    private final CommentReportRepository commentReportRepository;
//
//    public PostController(PostService postService,
//                          PostRepository postRepository,
//                          PostLikeRepository postLikeRepository,
//                          CommentRepository commentRepository,
//                          CommentLikeRepository commentLikeRepository,
//                          GoodsService goodsService,
//                          GoodsRepository goodsRepository,
//                          LegacyMemberService legacyMemberService,
//                          MemberRepository memberRepository,
//                          CommentService commentService,
//                          MentionService mentionService,
//                          TagService tagService,
//                          MessageService messageService,
//                          KeywordService keywordService,
//                          BlockService blockService,
//                          AttachmentService attachmentService,
//                          PostLabelRepository postLabelRepository,
//                          PostReportRepository postReportRepository,
//                          CommentReportRepository commentReportRepository) {
//        this.postService = postService;
//        this.postRepository = postRepository;
//        this.postLikeRepository = postLikeRepository;
//        this.commentRepository = commentRepository;
//        this.commentLikeRepository = commentLikeRepository;
//        this.goodsService = goodsService;
//        this.goodsRepository = goodsRepository;
//        this.legacyMemberService = legacyMemberService;
//        this.memberRepository = memberRepository;
//        this.commentService = commentService;
//        this.mentionService = mentionService;
//        this.tagService = tagService;
//        this.messageService = messageService;
//        this.keywordService = keywordService;
//        this.blockService = blockService;
//        this.attachmentService = attachmentService;
//        this.postLabelRepository = postLabelRepository;
//        this.postReportRepository = postReportRepository;
//        this.commentReportRepository = commentReportRepository;
//    }
//
//    @GetMapping
//    public CursorResponse getPosts(@RequestParam(defaultValue = "20") int count,
//                                   @RequestParam(required = false, defaultValue = "0") int category,
//                                   @RequestParam(required = false) String keyword,
//                                   @RequestParam(required = false) String cursor,
//                                   @RequestParam(defaultValue = "0") int label,
//                                   @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//
//        Member me = legacyMemberService.currentMember();
//        final Map<Long, Block> blackList = me != null ? blockService.getBlackListByMe(me.getId()) : null;
//
//        Slice<Post> posts = findPosts(count, category, label, keyword, cursor);
//        List<PostInfo> result = new ArrayList<>();
//
//        posts.stream().forEach(post -> {
//            List<GoodsInfo> goodsInfo = new ArrayList<>();
//            post.getGoods().forEach(goodsNo -> goodsService.generateGoodsInfo(goodsNo).ifPresent(goodsInfo::add));
//
//            PostInfo info = new PostInfo(post, legacyMemberService.getMemberInfo(post.getCreatedBy()), goodsInfo);
//
//            if (me != null) {
//                postLikeRepository.findByPostIdAndCreatedByIdAndStatus(post.getId(), me.getId(), LIKE)
//                        .ifPresent(like -> info.setLikeId(like.getId()));
//
//                Block block = blackList != null ? blackList.get(post.getCreatedBy().getId()) : null;
//                if (block != null) {
//                    info.setDescription(messageService.getMessage(POST_BLOCKED_MESSAGE, lang));
//                    info.setContents(new HashSet<>());
//                    info.setBlockId(block.getId());
//                }
//            }
//
//            log.debug("post info: {}", info);
//            result.add(info);
//        });
//
//        if (StringUtils.isNotBlank(keyword)) {
//            keyword = keyword.trim();
//            try {
//                keywordService.updateKeywordCount(keyword);
//                keywordService.logHistory(keyword, KeywordService.KeywordCategory.POST, me);
//            } catch (CannotAcquireLockException e) { // Ignore
//                log.warn("getPosts throws ConcurrencyFailureException: " + keyword);
//            }
//        }
//
//        String nextCursor = null;
//        if (result.size() > 0) {
//            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
//        }
//
//        return new CursorResponse.Builder<>("/api/1/posts", result)
//                .withCount(count)
//                .withCursor(nextCursor)
//                .withKeyword(keyword)
//                .withCategory(String.valueOf(category)).toBuild();
//    }
//
//    private Slice<Post> findPosts(int count, int category, int label, String keyword, String cursor) {
//        PageRequest page = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id"));
//        Slice<Post> posts;
//        Date dateCursor = null;
//
//        if (StringUtils.isNumeric(cursor)) {
//            dateCursor = new Date(Long.parseLong(cursor));
//        } else {
//            dateCursor = new Date();
//        }
//
//        if (category > 0 && label > 0) {
//            if (!StringUtils.isBlank(keyword)) {
//                posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndLabelIdAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(dateCursor, dateCursor, category, label, keyword, keyword, page);
//            } else {
//                posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndLabelIdAndDeletedAtIsNull(dateCursor, dateCursor, category, label, page);
//            }
//        } else if (category > 0) {
//            if (!StringUtils.isBlank(keyword)) {
//                posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(dateCursor, dateCursor, category, keyword, keyword, page);
//            } else {
//                posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndDeletedAtIsNull(dateCursor, dateCursor, category, page);
//            }
//        } else if (label > 0) {
//            if (!StringUtils.isBlank(keyword)) {
//                posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndLabelIdAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(dateCursor, dateCursor, label, keyword, keyword, page);
//            } else {
//                posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndLabelIdAndDeletedAtIsNull(dateCursor, dateCursor, label, page);
//            }
//        } else {
//            if (!StringUtils.isBlank(keyword)) {
//                posts = postRepository.searchPost(keyword, new Date(), dateCursor, page);
//            } else {
//                posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(dateCursor, dateCursor, page);
//            }
//        }
//
//        return posts;
//    }
//
//    @GetMapping("/{id:.+}")
//    public ResponseEntity<PostInfo> getPost(@PathVariable Long id,
//                                            @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Member me = legacyMemberService.currentMember();
//
//        final Map<Long, Block> blackList = me != null ? blockService.getBlackListByMe(me.getId()) : null;
//
//        Date now = new Date();
//        return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
//                .map(post -> {
//                    boolean blocked = me != null && blockService.isBlocked(me.getId(), post.getCreatedBy());
//                    List<GoodsInfo> goodsInfo = new ArrayList<>();
//                    post.getGoods().forEach(goodsNo -> goodsService.generateGoodsInfo(goodsNo).ifPresent(goodsInfo::add));
//
//                    PostInfo info = new PostInfo(post, legacyMemberService.getMemberInfo(post.getCreatedBy()), goodsInfo);
//                    log.debug("post info: {}", info);
//
//                    if (me != null) {
//                        postLikeRepository.findByPostIdAndCreatedById(post.getId(), me.getId())
//                                .ifPresent(like -> info.setLikeId(like.getId()));
//                    }
//
//                    if (blocked) {
//                        info.setDescription(messageService.getMessage(POST_BLOCKED_MESSAGE, lang));
//                        info.setContents(new HashSet<>());
//                    }
//
//                    return new ResponseEntity<>(info, HttpStatus.OK);
//                })
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//    }
//
//    @GetMapping("/{id:.+}/goods")
//    public ResponseEntity<List<GoodsInfo>> getGoods(@PathVariable Long id,
//                                                    @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Date now = new Date();
//        return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
//                .map(post -> {
//                    List<GoodsInfo> result = new ArrayList<>();
//                    post.getGoods().stream().forEach(gno -> {
//                        goodsRepository.findByGoodsNo(gno).ifPresent(g -> {
//                            result.add(goodsService.generateGoodsInfo(g));
//                        });
//                    });
//                    return new ResponseEntity<>(result, HttpStatus.OK);
//                })
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//    }
//
//    @GetMapping("/{id:.+}/winners")
//    public ResponseEntity<List<MemberInfo>> getWinners(@PathVariable Long id,
//                                                       @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Date now = new Date();
//        return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
//                .map(post -> {
//                    List<MemberInfo> result = new ArrayList<>();
//                    post.getWinners().stream().forEach(mid -> {
//                        memberRepository.findByIdAndDeletedAtIsNull(mid).ifPresent(m -> {
//                            result.add(legacyMemberService.getMemberInfo(m));
//                        });
//                    });
//                    return new ResponseEntity<>(result, HttpStatus.OK);
//                })
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//    }
//
//    @PostMapping("/{id:.+}/view_count")
//    public ResponseEntity<?> addViewCount(@PathVariable Long id,
//                                          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//
//        Date now = new Date();
//        return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
//                .map(post -> {
//                    postService.updateViewCount(post, 1);
//                    return new ResponseEntity(HttpStatus.OK);
//                })
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//    }
//
//    @PostMapping("/{id:.+}/likes")
//    public ResponseEntity<PostLikeInfo> addPostLike(@PathVariable Long id,
//                                                    @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Member me = legacyMemberService.currentMember();
//        Date now = new Date();
//        return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
//                .map(post -> {
//                    Long postId = post.getId();
//                    if (postLikeRepository.findByPostIdAndStatusAndCreatedById(postId, LIKE, me.getId()).isPresent()) {
//                        throw new BadRequestException("already_liked", messageService.getMessage(ALREADY_LIKED, lang));
//                    }
//
//                    PostLike postLike = postService.likePost(post, me.getId());
//                    return new ResponseEntity<>(new PostLikeInfo(postLike, legacyMemberService.getMemberInfo(me),
//                            legacyMemberService.getMemberInfo(post.getCreatedBy())), HttpStatus.OK);
//                })
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//    }
//
//    @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
//    public ResponseEntity<?> removePostLike(@PathVariable Long id,
//                                            @PathVariable Long likeId,
//                                            @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Long memberId = legacyMemberService.currentMemberId();
//        postLikeRepository.findByIdAndPostIdAndCreatedById(likeId, id, memberId)
//                .map(liked -> {
//                    postService.unLikePost(liked);
//                    return Optional.empty();
//                })
//                .orElseThrow(() -> new NotFoundException("like_not_found", messageService.getMessage(LIKE_NOT_FOUND, lang)));
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    @GetMapping("/{id:.+}/comments")
//    public CursorResponse getComments(@PathVariable Long id,
//                                      @RequestParam(defaultValue = "20") int count,
//                                      @RequestParam(required = false) Long cursor,
//                                      @RequestParam(required = false) String direction,
//                                      @RequestParam(name = "parent_id", required = false) Long parentId,
//                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Date now = new Date();
//        postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
//                .orElseThrow(() -> new NotFoundException("post_not_found", "post not found"));
//
//        PageRequest page;
//        if ("next".equals(direction)) {
//            page = PageRequest.of(0, count, Sort.by(Sort.Direction.ASC, "id"));
//        } else {
//            page = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id")); // default
//        }
//
//        Slice<Comment> comments;
//        Long me = legacyMemberService.currentMemberId();
//        Map<Long, Block> blackList = me != null ? blockService.getBlackListByMe(me) : new HashMap<>();
//
//        if (parentId != null) {
//            comments = postService.findCommentsByParentId(parentId, cursor, page, direction);
//        } else {
//            comments = postService.findCommentsByPostId(id, cursor, page, direction);
//        }
//
//        List<CommentInfo> result = new ArrayList<>();
//        comments.stream().forEach(comment -> {
//            CommentInfo commentInfo = null;
//            if (comment.getComment().contains("@")) {
//                MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
//                if (mentionResult != null) {
//                    String content = commentService.getBlindContent(comment, lang, mentionResult.getComment());
//                    comment.setComment(content);
//                    commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()), mentionResult.getMentionInfo());
//                } else {
//                    log.warn("mention result not found - {}", comment);
//                }
//            } else {
//                String content = commentService.getBlindContent(comment, lang, null);
//                comment.setComment(content);
//                commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()));
//            }
//
//            if (me != null) {
//                Long likeId = commentLikeRepository.findByCommentIdAndCreatedByIdAndStatus(comment.getId(), me, LIKE)
//                        .map(CommentLike::getId).orElse(null);
//                commentInfo.setLikeId(likeId);
//
//                Block block = blackList.get(commentInfo.getCreatedBy().getId());
//                if (block != null) {
//                    commentInfo.setBlockId(block.getId());
//                    commentInfo.setComment(messageService.getMessage(COMMENT_BLOCKED_MESSAGE, lang));
//                }
//            }
//            result.add(commentInfo);
//        });
//
//        String nextCursor = null;
//        if (result.size() > 0) {
//            if ("next".equals(direction)) {
//                nextCursor = String.valueOf(result.get(result.size() - 1).getId() + 1);
//            } else {
//                nextCursor = String.valueOf(result.get(result.size() - 1).getId() - 1);
//            }
//        }
//
//        int totalCount = postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
//                .map(Post::getCommentCount).orElse(0);
//
//        return new CursorResponse
//                .Builder<>("/api/1/posts/" + id + "/comments", result)
//                .withCount(count)
//                .withCursor(nextCursor)
//                .withTotalCount(totalCount).toBuild();
//    }
//
//    @GetMapping("/{postId}/comments/{id}")
//    public ResponseEntity<CommentInfo> getPostComment(@PathVariable long postId,
//                                                      @PathVariable long id,
//                                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//
//        Member member = legacyMemberService.currentMember();
//
//        CommentInfo commentInfo = postService.getPostComment(postId, id, member, lang);
//
//        return new ResponseEntity<>(commentInfo, HttpStatus.OK);
//    }
//
//    @PostMapping("/{id:.+}/comments")
//    public ResponseEntity addComment(@PathVariable Long id,
//                                     @RequestBody CreateCommentRequest request,
//                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
//                                     BindingResult bindingResult) {
//        if (bindingResult != null && bindingResult.hasErrors()) {
//            throw new BadRequestException(bindingResult.getFieldError());
//        }
//
//        Member member = legacyMemberService.currentMember();
//        if (!legacyMemberService.hasCommentPostPermission(member)) {
//            throw new BadRequestException("invalid_permission", messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
//        }
//
//        if (request.getParentId() != null) {
//            commentRepository.findById(request.getParentId())
//                    .orElseThrow(() -> new NotFoundException("comment_not_found", messageService.getMessage(COMMENT_NOT_FOUND, lang)));
//        }
//
//        Comment comment = commentService.addComment(request, CommentService.COMMENT_TYPE_POST, id, member);
//        return new ResponseEntity<>(new CommentInfo(comment), HttpStatus.OK);
//    }
//
//    @PatchMapping("/{postId:.+}/comments/{id:.+}")
//    public ResponseEntity updateComment(@PathVariable Long postId,
//                                        @PathVariable Long id,
//                                        @RequestBody UpdateCommentRequest request,
//                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
//                                        BindingResult bindingResult) {
//
//        if (bindingResult != null && bindingResult.hasErrors()) {
//            throw new BadRequestException(bindingResult.getFieldError());
//        }
//
//        Member member = legacyMemberService.currentMember();
//        if (!legacyMemberService.hasCommentPostPermission(member)) {
//            throw new BadRequestException("invalid_permission", messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
//        }
//
//        return commentRepository.findByIdAndPostIdAndCreatedById(id, postId, member.getId())
//                .map(comment -> {
//                    if (comment.getLocked()) {
//                        throw new BadRequestException("comment_locked", messageService.getMessage(COMMENT_LOCKED, lang));
//                    }
//                    comment = commentService.updateComment(request, comment);
//                    return new ResponseEntity<>(
//                            new CommentInfo(commentRepository.save(comment), legacyMemberService.getMemberInfo(comment.getCreatedBy())),
//                            HttpStatus.OK
//                    );
//                })
//                .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid post id or comment id"));
//    }
//
//    @DeleteMapping("/{postId:.+}/comments/{id:.+}")
//    public ResponseEntity<?> removeComment(@PathVariable Long postId,
//                                           @PathVariable Long id,
//                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        return commentRepository.findByIdAndPostIdAndCreatedById(id, postId, legacyMemberService.currentMemberId())
//                .map(comment -> {
//                    if (comment.getLocked()) {
//                        throw new BadRequestException("comment_locked", messageService.getMessage(COMMENT_LOCKED, lang));
//                    }
//                    int state = commentService.deleteComment(comment);
//                    return new ResponseEntity<>(new VideoController.CommentStateInfo(state), HttpStatus.OK);
//                })
//                .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid post id or comment id"));
//    }
//
//    @PostMapping("/{postId:.+}/comments/{commentId:.+}/likes")
//    public ResponseEntity<CommentLikeInfo> addCommentLike(@PathVariable Long postId,
//                                                          @PathVariable Long commentId,
//                                                          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Member member = legacyMemberService.currentMember();
//        try {
//            CommentLike commentLike = postService.likeCommentPost(commentId, postId, member);
//            return new ResponseEntity<>(new CommentLikeInfo(commentLike), HttpStatus.OK);
//        } catch (BadRequestException e) {
//            throw new BadRequestException("already_liked", messageService.getMessage(ALREADY_LIKED, lang));
//        }
//    }
//
//    @PatchMapping("/{postId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
//    public ResponseEntity<?> removeCommentLike(@PathVariable Long postId,
//                                               @PathVariable Long commentId,
//                                               @PathVariable Long likeId,
//                                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        Member me = legacyMemberService.currentMember();
//        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//
//        commentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, comment.getId(), me.getId())
//                .map(liked -> {
//                    postService.unLikeCommentPost(liked);
//                    return Optional.empty();
//                })
//                .orElseThrow(() -> new NotFoundException("comment_like_not_found", messageService.getMessage(LIKE_NOT_FOUND, lang)));
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    @GetMapping("/labels")
//    public ResponseEntity<?> getPostLabels() {
//        List<PostLabel> groups = postLabelRepository.findAll();
//        if (groups == null) {
//            return new ResponseEntity<>(Arrays.asList(), HttpStatus.OK);
//        }
//        List<PostLabelInfo> result = groups.stream().map(g -> new PostLabelInfo(g)).collect(Collectors.toList());
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
//
//    /**
//     * Report
//     */
//    @PostMapping(value = "/{id:.+}/report")
//    public ResponseEntity<PostInfo> reportVideo(@PathVariable Long id,
//                                                @Valid @RequestBody PostController.ReportRequest request,
//                                                @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        int reasonCode = (request.getReasonCode() == null ? 0 : request.getReasonCode());
//        Member me = legacyMemberService.currentMember();
//
//        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//
//        if (postReportRepository.findByPostIdAndCreatedById(id, me.getId()).isPresent()) {
//            throw new BadRequestException("already_reported", messageService.getMessage(POST_ALREADY_REPORTED, lang));
//        }
//
//        postService.reportPost(post, me, reasonCode, request.getReason());
//        return new ResponseEntity<>(new PostInfo(post, new MemberInfo(me), null), HttpStatus.OK);
//    }
//
//    @PostMapping(value = "/{postId:.+}/comments/{id:.+}/report")
//    public ResponseEntity<CommentReportInfo> reportPostComment(@PathVariable Long postId,
//                                                               @PathVariable Long id,
//                                                               @Valid @RequestBody ReportRequest request,
//                                                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//        int reasonCode = (request.getReasonCode() == null ? 0 : request.getReasonCode());
//        Member me = legacyMemberService.currentMember();
//
//        Comment comment = commentRepository.findByIdAndPostId(id, postId)
//                .orElseThrow(() -> new NotFoundException("comment_not_found", messageService.getMessage(COMMENT_NOT_FOUND, lang)));
//
//        Optional<CommentReport> alreadyCommentReport = commentReportRepository.findByCommentIdAndCreatedById(id, me.getId());
//        if (alreadyCommentReport.isPresent()) {
//            throw new ConflictException(messageService.getMessage(COMMENT_ALREADY_REPORTED, lang));
//        }
//
//        CommentReport report = commentService.reportComment(comment, me, reasonCode, request.getReason());
//        return new ResponseEntity<>(new CommentReportInfo(report), HttpStatus.OK);
//    }
//
//    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<PostInfo> savePost(PostRequest request,
//                                             @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//
//        log.debug("{}", request);
//        Member member = legacyMemberService.currentMember();
//        Post post = createPersonalPost(request);
//        postService.savePost(post, request.getFiles(), member);
//
//        return new ResponseEntity<>(new PostInfo(post), HttpStatus.OK);
//    }
//
//    @PostMapping(value = "/{id:.+}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<PostInfo> updatePost(@PathVariable Long id,
//                                               PostRequest request,
//                                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//
//        log.debug("{}", request);
//
//        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
//                .orElseThrow(() -> new NotFoundException("post_not_found", "post not found"));
//
//        updatePostProperties(post, request);
//        postService.updatePost(post, request.getFiles());
//
//        return new ResponseEntity<>(new PostInfo(post), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{id:.+}")
//    public ResponseEntity removePost(@PathVariable Long id,
//                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
//
//        Long memberId = legacyMemberService.currentMemberId();
//        log.debug("post id: {}, member id: {}", id, memberId);
//        Post post = postRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberId)
//                .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
//
//        postService.removePost(post);
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    private Post createPersonalPost(PostRequest request) {
//        Post post = new Post();
//        BeanUtils.copyProperties(request, post);
//
//        post.setTitle("");
//        post.setThumbnailUrl("");
//        post.setCategory(Post.CATEGORY_TREND);
//        post.setLabelId(request.getLabel());
//
//        post.setOpened(true);
//        Date startedAt = new Date();
//        Date endedAt = DateUtils.addYear(10);
//        post.setStartedAt(startedAt);
//        post.setEndedAt(endedAt);
//
//        return post;
//    }
//
//    private void updatePostProperties(Post post, PostRequest request) {
//        if (post.getLabelId() != request.getLabel()) {
//            post.setLabelId(request.getLabel());
//        }
//
//        if (!post.getDescription().equals(request.getDescription()) && !StringUtils.isBlank(request.getDescription())) {
//            post.setDescription(request.getDescription());
//        }
//    }
//
//    /**
//     * @see com.jocoos.mybeautip.post.Post
//     */
//    @Data
//    public static class PostInfo {
//        private Long id;
//        private String title;
//        private String bannerText;
//        private String description;
//        private String thumbnailUrl;
//        private int category;
//        private int label;
//        private int progress;
//        private Set<PostContent> contents;
//        @Deprecated
//        private List<String> goods;
//        private List<GoodsInfo> goodsInfo;
//        private Set<Long> winners;
//        private int likeCount;
//        private int commentCount;
//        private int viewCount;
//        private Date createdAt;
//        private MemberInfo createdBy;
//        private Long likeId;
//        private Long blockId;
//        private boolean opened;
//        private Date startedAt;
//        private Date endedAt;
//
//        public PostInfo(Post post, MemberInfo memberInfo, List<GoodsInfo> goodsInfo) {
//            this(post);
//            this.createdBy = memberInfo;
//            this.goodsInfo = goodsInfo;
//            this.label = post.getLabelId();
//        }
//
//        public PostInfo(Post post) {
//            BeanUtils.copyProperties(post, this);
//            this.createdBy = new MemberInfo(post.getCreatedBy());
//        }
//    }
//
//    @Data
//    public static class PostLikeInfo {
//        private Long id;
//        @Deprecated
//        private MemberInfo createdBy;
//        private Date createdAt;
//        private PostBasicInfo post;
//
//        public PostLikeInfo(PostLike postLike, MemberInfo likeMemberInfo, MemberInfo memberInfo) {
//            BeanUtils.copyProperties(postLike, this);
//            this.createdBy = likeMemberInfo;
//            post = new PostBasicInfo(postLike.getPost(), memberInfo);
//        }
//    }
//
//    @Data
//    public static class PostBasicInfo {
//        private Long id;
//        private String title;
//        private String description;
//        private int category;
//        private int label;
//        private int progress;
//        private String thumbnailUrl;
//        private Date createdAt;
//        private MemberInfo createdBy;
//        private int likeCount;
//        private Long likeId;
//
//        public PostBasicInfo(Post post, MemberInfo createdBy) {
//            BeanUtils.copyProperties(post, this);
//            this.createdBy = createdBy;
//            this.label = post.getLabelId();
//        }
//    }
//
//    @Data
//    public static class CommentLikeInfo {
//        private Long id;
//        @Deprecated
//        private MemberInfo createdBy;
//        private Date createdAt;
//        private CommentInfo comment;
//
//        public CommentLikeInfo(CommentLike commentLike) {
//            BeanUtils.copyProperties(commentLike, this);
//            comment = new CommentInfo(commentLike.getComment());
//        }
//    }
//
//    @Data
//    @NoArgsConstructor
//    public static class PostLabelInfo {
//        private Long id;
//        private String name;
//        private Date createdAt;
//
//        public PostLabelInfo(PostLabel postLabel) {
//            BeanUtils.copyProperties(postLabel, this);
//        }
//    }
//
//    @Data
//    private static class ReportRequest {
//        @Size(max = 80)
//        private String reason;
//
//        @NotNull
//        private Integer reasonCode;
//    }
//
//    @Data
//    public static class PostRequest {
//        @NotNull
//        private String description;
//        private int label;
//        private List<MultipartFile> files;
//    }
//}
