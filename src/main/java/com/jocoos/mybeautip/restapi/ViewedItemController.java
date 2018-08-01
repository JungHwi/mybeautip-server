package com.jocoos.mybeautip.restapi;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.post.ViewedPost;
import com.jocoos.mybeautip.post.ViewedPostRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/views")
public class ViewedItemController {

  private final int DAY_IN_MS = 1000 * 60 * 60 * 24;

  private final ViewedPostRepository viewedPostRepository;
  private final PostRepository postRepository;

  public ViewedItemController(ViewedPostRepository viewedPostRepository, PostRepository postRepository) {
    this.viewedPostRepository = viewedPostRepository;
    this.postRepository = postRepository;
  }

  /**
   * Return viewed posts for 7 days or max 100 items.
   * @param count counts for
   * @return Slice<ViewedPostInfo></ViewedPostInfo>
   */
  @GetMapping(params = "category=post")
  @ResponseBody
  public CursorResponse  findAllViewedPosts(@RequestParam(defaultValue = "20") int count,
                                                             @RequestParam String category,
                                                             @RequestParam(required = false) String cursor) {
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));

    Date now = null;
    if (!Strings.isNullOrEmpty(cursor)) {
      now = new Date(Long.parseLong(cursor));
    } else  {
      now = new Date();
    }

    Date weekAgo = new Date(now.getTime() - 7 * DAY_IN_MS);
    List<ViewedPostInfo> result = Lists.newArrayList();

    viewedPostRepository.findByCreatedAtBeforeAndCreatedAtAfter(now, weekAgo, page)
       .stream().forEach(viewedPost -> {
         result.add(
            postRepository.findById(viewedPost.getPostId())
               .map(post -> new ViewedPostInfo(viewedPost, post))
               .orElseGet(() -> new ViewedPostInfo(viewedPost, null)));
       });

    log.debug("{}", result);

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<ViewedPostInfo>("/api/1/views", result)
       .withCategory(category)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  @Data
  public static class ViewedPostInfo {
    private Long id;
    private Long postId;
    private Date createdAt;

    private PostController.PostBasicInfo post;

    public ViewedPostInfo(ViewedPost viewedPost, Post src) {
      BeanUtils.copyProperties(viewedPost, this);
      if (src != null) {
        post = new PostController.PostBasicInfo(src);
      }
    }
  }
}
