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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostContent;
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
  public ResponseEntity<PostInfo> createPost(@RequestBody CreatePostRequest request) {
    log.debug("request: {}", request.toString());

    Post post = new Post();
    BeanUtils.copyProperties(request, post);

    log.debug("post: {}", post);
    post = postRepository.save(post);

    log.debug("saved post: {}", post);
    PostInfo info = new PostInfo();
    BeanUtils.copyProperties(post, info);

    return new ResponseEntity<PostInfo>(info, HttpStatus.OK);
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
    private List<PostContent> contents;
    private List<String> goods;
  }

  @Data
  public static class PostInfo {
    private String title;
    private String bannerText;
    private String description;
    private String thumbnailUrl;
    private int category;
    private List<PostContent> contents;
    private List<String> goods;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private Long createdBy;
    private Date createdAt;
  }
}
