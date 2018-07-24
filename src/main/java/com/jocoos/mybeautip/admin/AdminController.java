package com.jocoos.mybeautip.admin;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostContent;
import com.jocoos.mybeautip.post.PostGoods;
import com.jocoos.mybeautip.post.PostRepository;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final PostRepository postRepository;

  public AdminController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @GetMapping("/posts")
  public ResponseEntity<Page<Post>> getPosts(Pageable pageable) {
    return new ResponseEntity<Page<Post>>(postRepository.findAll(pageable), HttpStatus.OK);
  }

  @PostMapping("/posts")
  public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request) {
    log.debug("{}", request.toString());

    Post post = new Post();
    BeanUtils.copyProperties(request, post);

    List<PostContent> contents = Lists.newArrayList();
    request.getContents().stream().forEach(content -> {
      log.debug("content: {}", content);
      PostContent postContent = new PostContent();
      BeanUtils.copyProperties(content, postContent);
      log.debug("post content: {}", postContent);
      contents.add(postContent);
    });

    request.setContents(null);
    post.setContents(contents);

    List<PostGoods> postGoodsList = Lists.newArrayList();
    request.getGoods().stream().forEach(goods -> {
      log.debug("goods: {}", goods);
      PostGoods postGoods = new PostGoods();
      BeanUtils.copyProperties(goods, postGoods);
      log.debug("post goods: {}", postGoods);
      postGoodsList.add(postGoods);
    });

    request.setGoods(null);
    post.setPostGoods(postGoodsList);

    log.debug("post: {}", post);
    post = postRepository.save(post);

    log.debug("saved post: {}", post);
    return new ResponseEntity<Post>(post, HttpStatus.OK);
  }

  @DeleteMapping("/posts/{id:.+}")
  public ResponseEntity<?> deletePost(@PathVariable Long id) {
    postRepository.findById(id).map(post -> {
      post.setDeletedAt(new Date());
      postRepository.save(post);
      return Optional.empty();
    }).orElseThrow(() -> new NotFoundException("post_not_found", "post not found"));

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Data
  public static class CreatePostRequest {
    private String title;
    private String bannerText;
    private String description;
    private String thumbnailUrl;
    private int category;
    private List<CreatePostContent> contents;
    private List<CreatePostGoods> goods;
  }

  @Data
  public static class CreatePostContent {
    private int priority;
    private int category;
    private String content;
  }

  @Data
  public static class CreatePostGoods {
    private int priority;
    private String goodsNo;
  }
}
