package com.jocoos.mybeautip.restapi;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.post.PostContent;
import com.jocoos.mybeautip.post.Trend;
import com.jocoos.mybeautip.post.TrendRepository;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/trends", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrendController {

  private final TrendRepository trendRepository;

  public TrendController(TrendRepository trendRepository) {
    this.trendRepository = trendRepository;
  }

  @GetMapping
  public ResponseEntity<List<TrendInfo>> getTrends(@RequestParam(defaultValue = "5") int count) {
    Slice<Trend> trends = trendRepository.findAll(PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));
    List<TrendInfo> result = Lists.newArrayList();

    trends.stream().forEach(t -> {
      TrendInfo trendInfo = new TrendInfo();
      BeanUtils.copyProperties(t.getPost(), trendInfo);
      log.debug("trend info: {}", trendInfo);
      result.add(trendInfo);
    });

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * @see com.jocoos.mybeautip.post.Post
   */
  @Data
  public static class TrendInfo {
    private Long id;
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
    private Date createdAt;
  }
}
