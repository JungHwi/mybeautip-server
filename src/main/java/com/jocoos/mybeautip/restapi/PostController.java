package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
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
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.post.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;
  private final GoodsRepository goodsRepository;
  private final MemberService memberService;

  public PostController(PostRepository postRepository,
                        PostLikeRepository postLikeRepository,
                        GoodsRepository goodsRepository,
                        MemberService memberService) {
    this.postRepository = postRepository;
    this.postLikeRepository = postLikeRepository;
    this.goodsRepository = goodsRepository;
    this.memberService = memberService;
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
      posts = postRepository.findByCategoryAndTitleContainingAndCreatedAtBeforeAndDeletedAtIsNull(category, keyword, dateCursor, page);
    } else if (!Strings.isNullOrEmpty(keyword) && dateCursor != null) {
      posts = postRepository.findByTitleContainingAndCreatedAtBeforeAndDeletedAtIsNull(keyword, dateCursor, page);
    } else if (category > 0 && !Strings.isNullOrEmpty(keyword)) {
      posts = postRepository.findByCategoryAndTitleContainingAndDeletedAtIsNull(category, keyword, page);
    } else if (category > 0 && dateCursor != null) {
      posts = postRepository.findByCategoryAndCreatedAtBeforeAndDeletedAtIsNull(category, dateCursor, page);
    } else if (dateCursor != null) {
      posts = postRepository.findByCreatedAtBeforeAndDeletedAtIsNull(dateCursor, page);
    } else if (!Strings.isNullOrEmpty(keyword)) {
      posts = postRepository.findByTitleContainingAndDeletedAtIsNull(keyword, page);
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
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
  }

  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<?> addViewCount(@PathVariable Long id) {

    // TODO: Add history using spring AOP!!
    return postRepository.findById(id)
       .map(post -> {
         postRepository.updateViewCount(post.getId(), 1L);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
  }

  @Transactional
  @PostMapping("/{id:.+}/likes")
  public ResponseEntity<PostLikeInfo> addTrendLike(@PathVariable Long id) {
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

         postRepository.updateLikeCount(id, 1L);
         PostLike postLike = postLikeRepository.save(new PostLike(postId));
         return new ResponseEntity<>(new PostLikeInfo(postLike), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
  }

  @Transactional
  @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeTrendLike(@PathVariable Long id,
                                           @PathVariable Long likeId){
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return postRepository.findById(id)
       .map(trend -> {
         Optional<PostLike> liked = postLikeRepository.findById(likeId);
         if (!liked.isPresent()) {
           throw new NotFoundException("like_not_found", "invalid post like id");
         }

         postLikeRepository.delete(liked.get());
         postRepository.updateLikeCount(id, -1L);
         return new ResponseEntity(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("trend_not_found", "invalid trend id"));
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
}
