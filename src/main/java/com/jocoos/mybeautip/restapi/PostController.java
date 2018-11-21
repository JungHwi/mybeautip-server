package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.comment.*;
import com.jocoos.mybeautip.member.mention.MentionResult;
import com.jocoos.mybeautip.member.mention.MentionService;
import com.jocoos.mybeautip.member.mention.MentionTag;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.post.*;
import com.jocoos.mybeautip.tag.TagService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

  private static final String COMMENT_NOT_FOUND = "comment.not_found";

  private static final String POST_NOT_FOUND = "post.not_found";
  
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
                        MessageService messageService) {
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
  }

  @GetMapping
  public CursorResponse getPosts(@RequestParam(defaultValue = "20") int count,
                                   @RequestParam(required = false, defaultValue = "0") int category,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String cursor) {

    Long memberId = memberService.currentMemberId();

    Slice<Post> posts = findPosts(count, category, keyword, cursor);
    List<PostInfo> result = Lists.newArrayList();

    posts.stream().forEach(post -> {
      PostInfo info = new PostInfo(post);
      log.debug("post info: {}", info);

      postLikeRepository.findByPostIdAndCreatedById(post.getId(), memberId)
         .ifPresent(like -> info.setLikeId(like.getId()));

      result.add(info);
    });

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
    }

    // FIXME: How to make a code gracefully
    if (category > 0 && !Strings.isNullOrEmpty(keyword) && dateCursor != null) {
      posts = postRepository.findByCategoryAndCreatedAtBeforeAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(category, dateCursor, keyword, keyword, page);
    } else if (!Strings.isNullOrEmpty(keyword) && dateCursor != null) {
      posts = postRepository.findByCreatedAtBeforeAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(dateCursor, keyword, keyword, page);
    } else if (category > 0 && !Strings.isNullOrEmpty(keyword)) {
      posts = postRepository.findByCategoryAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(category, keyword, keyword, page);
    } else if (category > 0 && dateCursor != null) {
      posts = postRepository.findByCategoryAndCreatedAtBeforeAndDeletedAtIsNull(category, dateCursor, page);
    } else if (dateCursor != null) {
      posts = postRepository.findByCreatedAtBeforeAndDeletedAtIsNull(dateCursor, page);
    } else if (!Strings.isNullOrEmpty(keyword)) {
      posts = postRepository.findByDeletedAtIsNullAndTitleContainingOrDescriptionContaining(keyword, keyword, page);
    } else if (category > 0){
      posts = postRepository.findByCategoryAndDeletedAtIsNull(category, page);
    } else {
      posts = postRepository.findAll(page);
    }

    return posts;
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<PostInfo> getPost(@PathVariable Long id,
                                          @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Long memberId = memberService.currentMemberId();
    return postRepository.findById(id)
       .map(post -> {
         PostInfo info = new PostInfo(post);
         log.debug("post info: {}", info);

         postLikeRepository.findByPostIdAndCreatedById(post.getId(), memberId)
            .ifPresent(like -> info.setLikeId(like.getId()));
         return new ResponseEntity<>(info, HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }

  @GetMapping("/{id:.+}/goods")
  public ResponseEntity<List<GoodsInfo>> getGoods(@PathVariable Long id,
                                                  @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return postRepository.findById(id)
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
    return postRepository.findById(id)
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

  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<?> addViewCount(@PathVariable Long id,
                                        @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {

    // TODO: Add history using spring AOP!!
    return postRepository.findById(id)
       .map(post -> {
         postRepository.updateViewCount(post.getId(), 1);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }

  @Transactional
  @PostMapping("/{id:.+}/likes")
  public ResponseEntity<PostLikeInfo> addPostLike(@PathVariable Long id,
                                                  @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Long memberId = memberService.currentMemberId();
    return postRepository.findById(id)
       .map(post -> {
         Long postId = post.getId();
         if (postLikeRepository.findByPostIdAndCreatedById(postId, memberId).isPresent()) {
           throw new BadRequestException("duplicated_post_like", "Already post liked");
         }

         postRepository.updateLikeCount(id, 1);
         post.setLikeCount(post.getLikeCount() + 1);
         PostLike postLike = postLikeRepository.save(new PostLike(post));
         return new ResponseEntity<>(new PostLikeInfo(postLike), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", messageService.getMessage(POST_NOT_FOUND, lang)));
  }

  @Transactional
  @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removePostLike(@PathVariable Long id,
                                          @PathVariable Long likeId){
    Long memberId = memberService.currentMemberId();
    return postLikeRepository.findByIdAndPostIdAndCreatedById(likeId, id, memberId)
       .map(post -> {
         Optional<PostLike> liked = postLikeRepository.findById(likeId);
         if (!liked.isPresent()) {
           throw new NotFoundException("like_not_found", "invalid post like id");
         }

         postLikeRepository.delete(liked.get());
         postRepository.updateLikeCount(id, -1);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id or like id"));
  }

  @GetMapping("/{id:.+}/comments")
  public CursorResponse getComments(@PathVariable Long id,
                                    @RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) Long cursor,
                                    @RequestParam(required = false) String direction,
                                    @RequestParam(required = false) Long parentId) {
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

    int totalCount = postRepository.findById(id).map(Post::getCommentCount).orElse(0);

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
                                   BindingResult bindingResult,
                                   @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    if (request.getParentId() != null) {
      commentRepository.findById(request.getParentId())
         .map(parent -> {
            commentRepository.updateCommentCount(parent.getId(), 1);
            return Optional.empty();
         })
         .orElseThrow(() -> new NotFoundException("comment_not_found", messageService.getMessage(COMMENT_NOT_FOUND, lang)));
    }

    Comment comment = new Comment();
    comment.setPostId(id);
    BeanUtils.copyProperties(request, comment);
    
    tagService.parseHashTagsAndToucheRefCount(comment.getComment());
    postRepository.updateCommentCount(id, 1);

    commentService.save(comment);

    List<MentionTag> mentionTags = request.getMentionTags();
    if (mentionTags != null && mentionTags.size() > 0) {
      mentionService.updatePostCommentWithMention(comment, mentionTags);
    }

    return new ResponseEntity<>(
       new CommentInfo(comment, memberService.getMemberInfo(comment.getCreatedBy())),
       HttpStatus.OK
    );
  }

  @PatchMapping("/{postId:.+}/comments/{id:.+}")
  public ResponseEntity updateComment(@PathVariable Long postId,
                                          @PathVariable Long id,
                                          @RequestBody UpdateCommentRequest request,
                                          BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long memberId = memberService.currentMemberId();
    return commentRepository.findByIdAndPostIdAndCreatedById(id, postId, memberId)
       .map(comment -> {
         comment.setComment(request.getComment());
         return new ResponseEntity<>(
            new CommentInfo(commentRepository.save(comment), memberService.getMemberInfo(comment.getCreatedBy())),
            HttpStatus.OK
         );
       })
       .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid post id or comment id"));
  }

  @Transactional
  @DeleteMapping("/{postId:.+}/comments/{id:.+}")
  public ResponseEntity<?> removeComment(@PathVariable Long postId,
                                             @PathVariable Long id) {
    postRepository.updateCommentCount(postId, -1);

    Long memberId = memberService.currentMemberId();
    return commentRepository.findByIdAndPostIdAndCreatedById(id, postId, memberId)
       .map(comment -> {
         if (comment.getParentId() != null) {
           commentRepository.updateCommentCount(comment.getParentId(), -1);
         }
         // FIXME: need to delete comment_likes relevant to comment
         commentRepository.delete(comment);
         return new ResponseEntity<>(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid post id or comment id"));
  }

  @Transactional
  @PostMapping("/{postId:.+}/comments/{commentId:.+}/likes")
  public ResponseEntity<CommentLikeInfo> addCommentLike(@PathVariable Long postId,
                                                                @PathVariable Long commentId) {
    Member member = memberService.currentMember();
    return commentRepository.findByIdAndPostId(commentId, postId)
       .map(comment -> {
         if (commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), member.getId()).isPresent()) {
           throw new BadRequestException("duplicated_post_like", "Already post liked");
         }
         
         commentRepository.updateLikeCount(comment.getId(), 1);
         comment.setLikeCount(comment.getLikeCount() + 1);
         CommentLike commentLikeLike = commentLikeRepository.save(new CommentLike(comment));
         return new ResponseEntity<>(new CommentLikeInfo(commentLikeLike), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("comment_not_found", "invalid post or comment id"));
  }

  @Transactional
  @DeleteMapping("/{postId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeCommentLike(@PathVariable Long postId,
                                                 @PathVariable Long commentId,
                                                 @PathVariable Long likeId){
    Member me = memberService.currentMember();
    Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id or comment id"));

    return commentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, comment.getId(), me.getId())
       .map(liked -> {
         commentLikeRepository.delete(liked);
         commentRepository.updateLikeCount(liked.getComment().getId(), -1);

         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("comment_like_not_found", "invalid post comment like id"));
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
    private List<String> goods;
    private Set<Long> winners;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private Date createdAt;
    private Member createdBy;
    private Long likeId;

    public PostInfo(Post post) {
      BeanUtils.copyProperties(post, this);
    }
  }

  @Data
  public static class PostLikeInfo {
    private Long id;
    private Member createdBy;
    private Date createdAt;
    private PostBasicInfo post;

    public PostLikeInfo(PostLike postLike) {
      BeanUtils.copyProperties(postLike, this);
      post = new PostBasicInfo(postLike.getPost());
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
    private Member createdBy;
    private int likeCount;
    private Long likeId;

    public PostBasicInfo(Post post) {
      BeanUtils.copyProperties(post, this);
    }
  }

  @Data
  public static class CreateCommentRequest {
    @NotNull @Size(max = 500)
    private String comment;

    private Long parentId;

    private List<MentionTag> mentionTags;
  }

  @Data
  public static class UpdateCommentRequest {
    @NotNull @Size(max = 500)
    private String comment;
  }

  @Data
  public static class CommentLikeInfo {
    private Long id;
    private MemberInfo createdBy;
    private Date createdAt;
    private CommentInfo comment;

    public CommentLikeInfo(CommentLike commentLike) {
      BeanUtils.copyProperties(commentLike, this);
      comment = new CommentInfo(commentLike.getComment());
    }
  }
}
