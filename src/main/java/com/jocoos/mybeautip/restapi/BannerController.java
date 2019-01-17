package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import com.jocoos.mybeautip.notification.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.banner.BannerRepository;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.post.PostRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/banners", produces = MediaType.APPLICATION_JSON_VALUE)
public class BannerController {

  private final MessageService messageService;
  private final BannerRepository bannerRepository;
  private final PostRepository postRepository;

  private static final String BANNER_NOT_FOUND = "banner.not_found";

  public BannerController(MessageService messageService,
                          BannerRepository bannerRepository, PostRepository postRepository) {
    this.messageService = messageService;
    this.bannerRepository = bannerRepository;
    this.postRepository = postRepository;
  }

  @GetMapping
  public ResponseEntity<List<BannerInfo>> getBanners(@RequestParam(defaultValue = "5") int count) {
    PageRequest pageRequest = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq"));
    Date now = new Date();
    Slice<Banner> banners = bannerRepository.findByStartedAtBeforeAndEndedAtAfterAndDeletedAtIsNull(now, now, pageRequest);
    List<BannerInfo> result = Lists.newArrayList();
    log.debug("now: {}", now);

    banners.stream()
       .forEach(b -> {
         log.debug("{}", b.getPost().getId());
         postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(b.getPost().getId(), now, now)
         .ifPresent(p -> result.add(new BannerInfo(b)));
          }
       );

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<?> addBannerViewCount(@PathVariable Long id,
                                              @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return bannerRepository.findById(id)
       .map(banner -> {
         bannerRepository.updateViewCount(id, 1L);
         return new ResponseEntity<>(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("banner_not_found", messageService.getMessage(BANNER_NOT_FOUND, lang)));
  }


  /**
   * @see com.jocoos.mybeautip.banner.Banner
   */
  @Data
  public static class BannerInfo {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private int category;
    private int seq;
    private String link;
    private String startedAt;
    private String endedAt;
    private Long viewCount;
    private Date createdAt;

    public BannerInfo(Banner banner) {
      BeanUtils.copyProperties(banner, this);
    }
  }
}
