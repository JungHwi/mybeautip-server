package com.jocoos.mybeautip.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.post.Trend;
import com.jocoos.mybeautip.post.TrendRepository;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminController {

  private final PostRepository postRepository;
  private final TrendRepository trendRepository;

  public AdminController(PostRepository postRepository,
                         TrendRepository trendRepository) {
    this.postRepository = postRepository;
    this.trendRepository = trendRepository;
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

  @PostMapping("/trends")
  public ResponseEntity<TrendInfo> createTrend(@RequestBody CreateTrendRequest request) throws ParseException {
    log.debug("request: {}", request);

    Trend trend = new Trend();
    BeanUtils.copyProperties(request, trend);
    log.debug("trend: {}", trend);

    return postRepository.findById(request.getPostId()).map(p -> {
      trend.setPost(p);

      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
      try {
        trend.setStartedAt(df.parse(request.getStartedAt()));
        trend.setEndedAt(df.parse(request.getEndedAt()));
      } catch (ParseException e) {
        log.error("invalid date format", e);
      }

      trendRepository.save(trend);

      TrendInfo info = new TrendInfo();
      BeanUtils.copyProperties(trend, info);
      return new ResponseEntity<>(info, HttpStatus.OK);
    })
    .orElseThrow(() -> new NotFoundException("post_not_found", "invalid post id"));
  }

  @Data
  public static class CreateTrendRequest {
    private Long postId;
    private int seq;
    private String startedAt;
    private String endedAt;
  }

  @Data
  public static class TrendInfo {
    private Long id;
    private Post post;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date endedAt;
  }
}
