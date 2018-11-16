package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

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

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.banner.BannerRepository;
import com.jocoos.mybeautip.exception.NotFoundException;

@RestController
@RequestMapping(value = "/api/1/banners", produces = MediaType.APPLICATION_JSON_VALUE)
public class BannerController {

  private final BannerRepository bannerRepository;

  public BannerController(BannerRepository bannerRepository) {
    this.bannerRepository = bannerRepository;
  }

  @GetMapping
  public ResponseEntity<List<BannerInfo>> getBanners(@RequestParam(defaultValue = "5") int count) {
    Slice<Banner> banners = bannerRepository.findAll(PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));
    List<BannerInfo> result = Lists.newArrayList();

    banners.stream().forEach(b -> result.add(new BannerInfo(b)));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Transactional
  @PostMapping("/{id:.+}/view_count")
  public ResponseEntity<?> addBannerViewCount(@PathVariable Long id) {
    return bannerRepository.findById(id)
       .map(banner -> {
         bannerRepository.updateViewCount(id, 1L);
         return new ResponseEntity<>(HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("banner_not_found", "invalid banner id"));
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
