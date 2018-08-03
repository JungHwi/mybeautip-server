package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.post.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

  private final PostService postService;
  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;
  private final PostCommentRepository postCommentRepository;
  private final GoodsRepository goodsRepository;
  private final MemberService memberService;
  private final MemberRepository memberRepository;

  public PostController(PostService postService,
                        PostRepository postRepository,
                        PostLikeRepository postLikeRepository,
                        PostCommentRepository postCommentRepository,
                        GoodsRepository goodsRepository,
                        MemberService memberService,
                        MemberRepository memberRepository) {
    this.postService = postService;
    this.postRepository = postRepository;
    this.postLikeRepository = postLikeRepository;
    this.postCommentRepository = postCommentRepository;
    this.goodsRepository = goodsRepository;
    this.memberService = memberService;
    this.memberRepository = memberRepository;
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
      PostInfo info = new PostInfo();
      BeanUtils.copyProperties(post, info);
      log.debug("post info: {}", info);

      postLikeRepository.findByPostIdAndCreatedBy(post.getId(), memberId)
         .ifPresent(like -> info.setLikeId(like.getId()));

      result.add(info);
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder("/api/1/posts", result)
       .withCount(count)
       .withCursor(nextCursor)
       .withKeyword(keyword)
       .withCategory(String.valueOf(category)).toBuild();
  }

  private Slice<Post> findPosts(int count, int category, String keyword, String cursor) {
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Post> posts = null;
    Date dateCursor = null;

    if (!Strings.isNullOrEmpty(cursor) && StringUtils.isNumeric(cursor)) {
      dateCursor = new Date(Long.parseLong(cursor));
    }

    // FIXME: How to make a code gracefully
    if (category > 0 && !Strings.isNullOrEmpty(keyword) && dateCursor != null) {
      posts = postRepository.findByCategoryAndTitleContainingOrDescriptionContainingAndCreatedAtBeforeAndDeletedAtIsNull(category, keyword, keyword, dateCursor, page);
    } else if (!Strings.isNullOrEmpty(keyword) && dateCursor != null) {
      posts = postRepository.findByTitleContainingOrDescriptionContainingAndCreatedAtBeforeAndDeletedAtIsNull(keyword, keyword, dateCursor, page);
    } else if (category > 0 && !Strings.isNullOrEmpty(keyword)) {
      posts = postRepository.findByCategoryAndTitleContainingOrDescriptionContainingAndDeletedAtIsNull(category, keyword, keyword, page);
    } else if (category > 0 && dateCursor != null) {
      posts = postRepository.findByCategoryAndCreatedAtBeforeAndDeletedAtIsNull(category, dateCursor, page);
    } else if (dateCursor != null) {
      posts = postRepository.findByCreatedAtBeforeAndDeletedAtIsNull(dateCursor, page);
    } else if (!Strings.isNullOrEmpty(keyword)) {
      posts = postRepository.findByTitleContainingOrDescriptionContainingAndDeletedAtIsNull(keyword, keyword, page);
    } else if (category > 0){
      posts = postRepository.findByCategoryAndDeletedAtIsNull(category, page);
    } else {
      posts = postRepository.findAll(page);
    }

    return posts;
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<List<PostInfo>> getPost(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    return postRepository.findById(id)
       .map(post -> {
         PostInfo info = new PostInfo();
         BeanUtils.copyProperties(post, info);
         log.debug("post info: {}", info);

         postLikeRepository.findByPostIdAndCreatedBy(post.getId(), memberId)
            .ifPresent(like -> info.setLikeId(like.getId()));
         return new ResponseEntity(info, HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));
  }

  @GetMapping("/{id:.+}/goods")
  public ResponseEntity<List<GoodsInfo>> getGoods(@PathVariable Long id) {
    return postRepository.findById(id)
       .map(post -> {
         List<GoodsInfo> result = Lists.newArrayList();
         post.getGoods().stream().forEach(gno -> {
           goodsRepository.findById(gno).ifPresent(g -> {
             result.add(new GoodsInfo(g));
           });
         });
         return new ResponseEntity<>(result, HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));
  }

  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<?> addViewCount(@PathVariable Long id) {

    // TODO: Add history using spring AOP!!
    return postRepository.findById(id)
       .map(post -> {
         postRepository.updateViewCount(post.getId(), 1);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));
  }

  @Transactional
  @PostMapping("/{id:.+}/likes")
  public ResponseEntity<PostLikeInfo> addPostLike(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return postRepository.findById(id)
       .map(post -> {
         Long postId = post.getId();
         if (postLikeRepository.findByPostIdAndCreatedBy(postId, memberId).isPresent()) {
           throw new BadRequestException("duplicated_post_like", "Already post liked");
         }

         postRepository.updateLikeCount(id, 1);
         PostLike postLike = postLikeRepository.save(new PostLike(postId));
         return new ResponseEntity<>(new PostLikeInfo(postLike), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));
  }

  @Transactional
  @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removePostLike(@PathVariable Long id,
                                          @PathVariable Long likeId){
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return postRepository.findById(id)
       .map(post -> {
         Optional<PostLike> liked = postLikeRepository.findById(likeId);
         if (!liked.isPresent()) {
           throw new NotFoundException("like_not_found", "invalid post like id");
         }

         postLikeRepository.delete(liked.get());
         postRepository.updateLikeCount(id, -1);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));
  }

  @GetMapping("/{id:.+}/comments")
  public CursorResponse getPostComments(@PathVariable Long id,
                                        @RequestParam(defaultValue = "20") int count,
                                        @RequestParam(required = false) String cursor,
                                        @RequestParam(required = false) Long parentId) {
    PageRequest page = PageRequest.of(0, count);
    Slice<PostComment> comments = null;

    if (parentId != null) {
      comments = postService.findCommentsByParentId(parentId, cursor, page);
    } else {
      comments = postService.findCommentsByPostId(id, cursor, page);
    }

    List<PostCommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> {
      result.add(
         memberRepository.findById(comment.getCreatedBy())
            .map(member -> new PostCommentInfo(comment, member))
            .orElseGet(() -> new PostCommentInfo(comment))
      );
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse
       .Builder<PostCommentInfo>("/api/1/posts/" + id + "/comments", result)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  @Transactional
  @PostMapping("/{id:.+}/comments")
  public ResponseEntity addPostComment(@PathVariable Long id,
                                       @RequestBody CreateCommentRequest request,
                                       BindingResult bindingResult) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      new BadRequestException(bindingResult.getFieldError());
    }

    if (request.getParentId() != null) {
      postCommentRepository.findById(request.getParentId())
         .map(parent -> {
            postCommentRepository.updateCommentCount(parent.getId(), 1);
            return Optional.empty();
         })
         .orElseThrow(() -> new NotFoundException("comment_id_not_found", "invalid comment parent id"));
    }

    PostComment postComment = new PostComment(id);
    BeanUtils.copyProperties(request, postComment);
    postRepository.updateCommentCount(id, 1);

    return new ResponseEntity(
       new PostCommentInfo(postCommentRepository.save(postComment)),
       HttpStatus.OK
    );
  }

  @PatchMapping("/{postId:.+}/comments/{id:.+}")
  public ResponseEntity updatePostComment(@PathVariable Long postId,
                                          @PathVariable Long id,
                                          @RequestBody UpdateCommentRequest request,
                                          BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      new BadRequestException(bindingResult.getFieldError());
    }

    Long memberId = memberService.currentMemberId();
    return postCommentRepository.findById(id)
       .filter(comment -> comment.getCreatedBy().equals(memberId))
       .map(comment -> {
         comment.setComment(request.getComment());
         return new ResponseEntity(
            new PostCommentInfo(postCommentRepository.save(comment)),
            HttpStatus.OK
         );
       })
       .orElseThrow(() -> new NotFoundException("post_comment_not_found", "invalid comment id"));
  }

  @Transactional
  @DeleteMapping("/{postId:.+}/comments/{id:.+}")
  public ResponseEntity<?> removePostComment(@PathVariable Long postId,
                                             @PathVariable Long id) {
    postRepository.updateCommentCount(postId, -1);

    Long memberId = memberService.currentMemberId();
    return postCommentRepository.findById(id)
       .filter(comment -> comment.getCreatedBy().equals(memberId))
       .map(comment -> {
         if (comment.getParentId() != null) {
           postCommentRepository.updateCommentCount(comment.getParentId(), -1);
         }
         postCommentRepository.delete(comment);
         return new ResponseEntity<>(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("post_comment_not_found", "invalid comment id"));
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
    private Set<PostContent> contents;
    private List<String> goods;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private Date createdAt;
    private Long likeId;
  }

  @Data
  public static class PostLikeInfo {
    private Long id;
    private Date createdAt;

    public PostLikeInfo(PostLike postLike) {
      BeanUtils.copyProperties(postLike, this);
    }
  }

  @Data
  public static class PostBasicInfo {
    private Long id;
    private String title;
    private int category;
    private String thumbnailUrl;
    private Date createdAt;

    public PostBasicInfo(Post post) {
      BeanUtils.copyProperties(post, this);
    }
  }

  @Data
  public static class CreateCommentRequest {
    @NotNull @Size(max = 500)
    private String comment;

    private Long parentId;
  }

  @Data
  public static class UpdateCommentRequest {
    @NotNull @Size(max = 500)
    private String comment;
  }

  @Data
  public static class PostCommentInfo {
    private Long id;
    private Long postId;
    private String comment;
    private Long parentId;
    private int commentCount;
    private Long createdBy;
    private Date createdAt;
    private MemberInfo owner;
    private String commentRef;

    public PostCommentInfo(PostComment comment) {
      BeanUtils.copyProperties(comment, this);
      setCommentRef(comment);
    }

    public PostCommentInfo(PostComment comment, Member member) {
      this(comment);
      this.owner = new MemberInfo(member);
      setCommentRef(comment);
    }

    private void setCommentRef(PostComment comment) {
      if (comment != null && comment.getCommentCount() > 0) {
        this.commentRef = String.format("/api/1/posts/%d/comments?parentId=%d", comment.getPostId(), comment.getId());
      }
    }
  }
}
