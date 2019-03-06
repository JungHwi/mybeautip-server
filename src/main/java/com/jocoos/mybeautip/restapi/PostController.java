package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.comment.CreateCommentRequest;
import com.jocoos.mybeautip.comment.UpdateCommentRequest;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentInfo;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.comment.CommentService;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostContent;
import com.jocoos.mybeautip.post.PostLike;
import com.jocoos.mybeautip.post.PostLikeRepository;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.post.PostService;
import com.jocoos.mybeautip.search.KeywordService;
import com.jocoos.mybeautip.tag.TagService;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {
  private final PostService postService;
  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final GoodsService goodsService;
  private final GoodsRepository goodsRepository;
  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final CommentService commentService;
  private final MentionService mentionService;
  private final TagService tagService;
  private final MessageService messageService;
  private final KeywordService keywordService;

  private static final String COMMENT_NOT_FOUND = "comment.not_found";
  private static final String POST_NOT_FOUND = "post.not_found";
  private static final String ALREADY_LIKED = "like.already_liked";
  private static final String COMMENT_WRITE_NOT_ALLOWED = "comment.write_not_allowed";
  private static final String COMMENT_LOCKED = "comment.locked";
  
  public PostController(PostService postService,
                        PostRepository postRepository,
                        PostLikeRepository postLikeRepository,
                        CommentRepository commentRepository,
                        CommentLikeRepository commentLikeRepository,
                        GoodsService goodsService,
                        GoodsRepository goodsRepository,
                        MemberService memberService,
                        MemberRepository memberRepository,
                        CommentService commentService,
                        MentionService mentionService,
                        TagService tagService,
                        MessageService messageService,
                        KeywordService keywordService) {
    this.postService = postService;
    this.postRepository = postRepository;
    this.postLikeRepository = postLikeRepository;
    this.commentRepository = commentRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.goodsService = goodsService;
    this.goodsRepository = goodsRepository;
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.commentService = commentService;
    this.mentionService = mentionService;
    this.tagService = tagService;
    this.messageService = messageService;
    this.keywordService = keywordService;
  }
  
  @GetMapping
  public CursorResponse getPosts(@RequestParam(defaultValue = "20") int count,
                                   @RequestParam(required = false, defaultValue = "0") int category,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String cursor) {

    Member me = memberService.currentMember();

    Slice<Post> posts = findPosts(count, category, keyword, cursor);
    List<PostInfo> result = Lists.newArrayList();

    posts.stream().forEach(post -> {
      List<GoodsInfo> goodsInfo = new ArrayList<>();
      post.getGoods().forEach(goodsNo -> goodsService.generateGoodsInfo(goodsNo).ifPresent(goodsInfo::add));
      
      PostInfo info = new PostInfo(post, memberService.getMemberInfo(post.getCreatedBy()), goodsInfo);
      log.debug("post info: {}", info);

      if (me != null) {
        postLikeRepository.findByPostIdAndCreatedById(post.getId(), me.getId())
            .ifPresent(like -> info.setLikeId(like.getId()));
      }
      result.add(info);
    });
  
    if (StringUtils.isNotBlank(keyword)) {
      keyword = keyword.trim();
      keywordService.updateKeywordCount(keyword);
      keywordService.logHistory(keyword, KeywordService.KeywordCategory.POST, me);
    }

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/posts", result)
       .withCount(count)
       .withCursor(nextCursor)
       .withKeyword(keyword)
       .withCategory(String.valueOf(category)).toBuild();
  }

  private Slice<Post> findPosts(int count, int category, String keyword, String cursor) {
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Post> posts;
    Date dateCursor = null;

    if (StringUtils.isNumeric(cursor)) {
      dateCursor = new Date(Long.parseLong(cursor));
    } else {
      dateCursor = new Date();
    }


    if (category > 0) {
      if (!Strings.isNullOrEmpty(keyword)) {
        posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(dateCursor, dateCursor, category, keyword, keyword, page);
      } else {
        posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndDeletedAtIsNull(dateCursor, dateCursor, category, page);
      }
    } else {
      if (!Strings.isNullOrEmpty(keyword)) {
        posts = postRepository.searchPost(keyword, new Date(), dateCursor, page);
      } else {
        posts = postRepository.findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(dateCursor, dateCursor, page);
      }
    }

    return posts;
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<PostInfo> getPost(@PathVariable Long id,
                                          @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Long memberId = memberService.currentMemberId();
    Date now = new Date();
    return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
       .map(post -> {
         List<GoodsInfo> goodsInfo = new ArrayList<>();
         post.getGoods().forEach(goodsNo -> goodsService.generateGoodsInfo(goodsNo).ifPresent(goodsInfo::add));
         
         PostInfo info = new PostInfo(post, memberService.getMemberInfo(post.getCreatedBy()), goodsInfo);
         log.debug("post info: {}", info);
      
         if (memberId != null) {
           postLikeRepository.findByPostIdAndCreatedById(post.getId(), memberId)
               .ifPresent(like -> info.setLikeId(like.getId()));
         }
         return new ResponseEntity<>(info, HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }

  // TODO: will be deprecated
  @GetMapping("/{id:.+}/goods")
  public ResponseEntity<List<GoodsInfo>> getGoods(@PathVariable Long id,
                                                  @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Date now = new Date();
    return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
       .map(post -> {
         List<GoodsInfo> result = Lists.newArrayList();
         post.getGoods().stream().forEach(gno -> {
           goodsRepository.findByGoodsNo(gno).ifPresent(g -> {
             result.add(goodsService.generateGoodsInfo(g));
           });
         });
         return new ResponseEntity<>(result, HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }

  @GetMapping("/{id:.+}/winners")
  public ResponseEntity<List<MemberInfo>> getWinners(@PathVariable Long id,
                                                     @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Date now = new Date();
    return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
       .map(post -> {
         List<MemberInfo> result = Lists.newArrayList();
         post.getWinners().stream().forEach(mid -> {
           memberRepository.findByIdAndDeletedAtIsNull(mid).ifPresent(m -> {
             result.add(memberService.getMemberInfo(m));
           });
         });
         return new ResponseEntity<>(result, HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }

  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<?> addViewCount(@PathVariable Long id,
                                        @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {

    // TODO: Add history using spring AOP!!
    Date now = new Date();
    return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
       .map(post -> {
         postService.updateViewCount(post, 1);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }
  
  @PostMapping("/{id:.+}/likes")
  public ResponseEntity<PostLikeInfo> addPostLike(@PathVariable Long id,
                                                  @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member me = memberService.currentMember();
    Date now = new Date();
    return postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
       .map(post -> {
         Long postId = post.getId();
         if (postLikeRepository.findByPostIdAndCreatedById(postId, me.getId()).isPresent()) {
           throw new BadRequestException("already_liked", messageService.getMessage(ALREADY_LIKED, lang));
         }
         
         PostLike postLike = postService.likePost(post);
         return new ResponseEntity<>(new PostLikeInfo(postLike, memberService.getMemberInfo(me),
             memberService.getMemberInfo(post.getCreatedBy())), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }
  
  @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removePostLike(@PathVariable Long id,
                                          @PathVariable Long likeId){
    Long memberId = memberService.currentMemberId();
    postLikeRepository.findByIdAndPostIdAndCreatedById(likeId, id, memberId)
       .map(liked -> {
         postService.unLikePost(liked);
         return Optional.empty();
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id or like id"));
  
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("/{id:.+}/comments")
  public CursorResponse getComments(@PathVariable Long id,
                                    @RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) Long cursor,
                                    @RequestParam(required = false) String direction,
                                    @RequestParam(required = false) Long parentId) {
    Date now = new Date();
    postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
        .orElseThrow(() -> new NotFoundException("post_not_found", "post not found"));
    
    PageRequest page;
    if ("next".equals(direction)) {
      page = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "id"));
    } else {
      page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id")); // default
    }
    
    Slice<Comment> comments;
    if (parentId != null) {
      comments = postService.findCommentsByParentId(parentId, cursor, page, direction);
    } else {
      comments = postService.findCommentsByPostId(id, cursor, page, direction);
    }

    List<CommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> {
        CommentInfo commentInfo = null;
      if (comment.getComment().contains("@")) {
        MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
        if (mentionResult != null) {
          comment.setComment(mentionResult.getComment());
          commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()), mentionResult.getMentionInfo());
        } else {
          log.warn("mention result not found - {}", comment);
        }
      } else {
        commentInfo = new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy()));
      }
  
      Long me = memberService.currentMemberId();
      if (me != null) {
        Long likeId = commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), me)
           .map(CommentLike::getId).orElse(null);
        commentInfo.setLikeId(likeId);
      }
      result.add(commentInfo);
    });

    String nextCursor = null;
    if (result.size() > 0) {
      if ("next".equals(direction)) {
        nextCursor = String.valueOf(result.get(result.size() - 1).getId() + 1);
      } else {
        nextCursor = String.valueOf(result.get(result.size() - 1).getId() - 1);
      }
    }

    int totalCount = postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(id, now, now)
       .map(Post::getCommentCount).orElse(0);

    return new CursorResponse
      .Builder<>("/api/1/posts/" + id + "/comments", result)
      .withCount(count)
      .withCursor(nextCursor)
      .withTotalCount(totalCount).toBuild();
  }

  @Transactional
  @PostMapping("/{id:.+}/comments")
  public ResponseEntity addComment(@PathVariable Long id,
                                   @RequestBody CreateCommentRequest request,
                                   @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang,
                                   BindingResult bindingResult) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
  
    Member member = memberService.currentMember();
    if (!memberService.hasCommentPostPermission(member)) {
      throw new BadRequestException("invalid_permission", messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
    }

    if (request.getParentId() != null) {
      commentRepository.findById(request.getParentId())
         .orElseThrow(() -> new NotFoundException("comment_not_found", messageService.getMessage(COMMENT_NOT_FOUND, lang)));
    }
  
    Comment comment = commentService.addComment(request, CommentService.COMMENT_TYPE_POST, id);
    return new ResponseEntity<>(new CommentInfo(comment), HttpStatus.OK);
  }

  @PatchMapping("/{postId:.+}/comments/{id:.+}")
  public ResponseEntity updateComment(@PathVariable Long postId,
                                      @PathVariable Long id,
                                      @RequestBody UpdateCommentRequest request,
                                      @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang,
                                      BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
  
    Member member = memberService.currentMember();
    if (!memberService.hasCommentPostPermission(member)) {
      throw new BadRequestException("invalid_permission", messageService.getMessage(COMMENT_WRITE_NOT_ALLOWED, lang));
    }

    return commentRepository.findByIdAndPostIdAndCreatedById(id, postId, member.getId())
       .map(comment -> {
         if (comment.getLocked()) {
           throw new BadRequestException("comment_locked", messageService.getMessage(COMMENT_LOCKED, lang));
         }
         comment = commentService.updateComment(request, comment);
         return new ResponseEntity<>(
            new CommentInfo(commentRepository.save(comment), memberService.getMemberInfo(comment.getCreatedBy())),
            HttpStatus.OK
         );
       })
       .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid post id or comment id"));
  }
  
  @DeleteMapping("/{postId:.+}/comments/{id:.+}")
  public ResponseEntity<?> removeComment(@PathVariable Long postId,
                                         @PathVariable Long id,
                                         @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return commentRepository.findByIdAndPostIdAndCreatedById(id, postId, memberService.currentMemberId())
       .map(comment -> {
         if (comment.getLocked()) {
           throw new BadRequestException("comment_locked", messageService.getMessage(COMMENT_LOCKED, lang));
         }
         postService.deleteComment(comment);
         tagService.removeHistory(comment.getComment(), TagService.TAG_COMMENT, comment.getId(), comment.getCreatedBy());
         return new ResponseEntity<>(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid post id or comment id"));
  }

  @PostMapping("/{postId:.+}/comments/{commentId:.+}/likes")
  public ResponseEntity<CommentLikeInfo> addCommentLike(@PathVariable Long postId,
                                                        @PathVariable Long commentId,
                                                        @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member member = memberService.currentMember();
    return commentRepository.findByIdAndPostId(commentId, postId)
       .map(comment -> {
         if (commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), member.getId()).isPresent()) {
           throw new BadRequestException("already_liked", messageService.getMessage(ALREADY_LIKED, lang));
         }
         CommentLike commentLike = postService.likeCommentPost(comment);
         return new ResponseEntity<>(new CommentLikeInfo(commentLike), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("comment_like_not_found", "invalid post or comment id"));
  }

  @DeleteMapping("/{postId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeCommentLike(@PathVariable Long postId,
                                                 @PathVariable Long commentId,
                                                 @PathVariable Long likeId){
    Member me = memberService.currentMember();
    Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id or comment id"));

    commentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, comment.getId(), me.getId())
       .map(liked -> {
         postService.unLikeCommentPost(liked);
         return Optional.empty();
       })
       .orElseThrow(() -> new NotFoundException("comment_like_not_found", "invalid post comment like id"));
  
    return new ResponseEntity(HttpStatus.OK);
  }

  /**
   * @see com.jocoos.mybeautip.post.Post
   */
  @Data
  public static class PostInfo {
    private Long id;
    private String title;
    private String bannerText;
    private String description;
    private String thumbnailUrl;
    private int category;
    private int progress;
    private Set<PostContent> contents;
    private List<String> goods; // deprecated
    private List<GoodsInfo> goodsInfo;
    private Set<Long> winners;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private Date createdAt;
    private MemberInfo createdBy;
    private Long likeId;
    private boolean opened;
    private Date startedAt;
    private Date endedAt;

    public PostInfo(Post post, MemberInfo memberInfo, List<GoodsInfo> goodsInfo) {
      this(post);
      this.createdBy = memberInfo;
      this.goodsInfo = goodsInfo;
    }

    public PostInfo(Post post) {
      BeanUtils.copyProperties(post, this);
    }
  }

  @Data
  public static class PostLikeInfo {
    private Long id;
    private MemberInfo createdBy; // deprecated
    private Date createdAt;
    private PostBasicInfo post;

    public PostLikeInfo(PostLike postLike, MemberInfo likeMemberInfo, MemberInfo memberInfo) {
      BeanUtils.copyProperties(postLike, this);
      this.createdBy = likeMemberInfo;
      post = new PostBasicInfo(postLike.getPost(), memberInfo);
    }
  }

  @Data
  public static class PostBasicInfo {
    private Long id;
    private String title;
    private String description;
    private int category;
    private int progress;
    private String thumbnailUrl;
    private Date createdAt;
    private MemberInfo createdBy;
    private int likeCount;
    private Long likeId;

    public PostBasicInfo(Post post, MemberInfo createdBy) {
      BeanUtils.copyProperties(post, this);
      this.createdBy = createdBy;
    }
  }

  @Data
  public static class CommentLikeInfo {
    private Long id;
    private MemberInfo createdBy; // deprecated
    private Date createdAt;
    private CommentInfo comment;

    public CommentLikeInfo(CommentLike commentLike) {
      BeanUtils.copyProperties(commentLike, this);
      comment = new CommentInfo(commentLike.getComment());
    }
  }
}
