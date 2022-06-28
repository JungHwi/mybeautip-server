package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.banner.BannerRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.post.PostRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/banners", produces = MediaType.APPLICATION_JSON_VALUE)
@Deprecated
public class LegacyBannerController {

    private static final String BANNER_NOT_FOUND = "banner.not_found";
    private final MessageService messageService;
    private final BannerRepository bannerRepository;
    private final PostRepository postRepository;

    public LegacyBannerController(MessageService messageService,
                                  BannerRepository bannerRepository, PostRepository postRepository) {
        this.messageService = messageService;
        this.bannerRepository = bannerRepository;
        this.postRepository = postRepository;
    }

    @GetMapping
    public ResponseEntity<List<BannerInfo>> getBanners(@RequestParam(defaultValue = "20") int count,
                                                       @RequestParam(required = false) Set<Integer> categories) {
        return getBanners0(count, categories, false);
    }

    @GetMapping("/slim")
    public ResponseEntity<List<BannerInfo>> getSlimBanners(@RequestParam(defaultValue = "20") int count,
                                                           @RequestParam(required = false) Set<Integer> categories) {
        return getBanners0(count, categories, true);
    }

    private ResponseEntity<List<BannerInfo>> getBanners0(int count, Set<Integer> categories, boolean isSlim) {
        PageRequest pageRequest = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "seq", "createdAt"));
        Date now = new Date();
        Slice<Banner> banners = bannerRepository.findByStartedAtBeforeAndEndedAtAfterAndDeletedAtIsNull(now, now, pageRequest);
        List<BannerInfo> result = new ArrayList<>();
        log.debug("now: {}", now);

        banners.stream()
                .forEach(b -> {
                            postRepository.findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(b.getPost().getId(), now, now)
                                    .ifPresent(p -> {
                                        if (categories == null || categories.contains(p.getCategory())) {
                                            if (isSlim && b.getSlimThumbnailUrl() != null) {
                                                result.add(new BannerInfo(b, new PostController.PostInfo(p), b.getSlimThumbnailUrl()));
                                            } else {
                                                result.add(new BannerInfo(b, new PostController.PostInfo(p)));
                                            }
                                        }
                                    });
                        }
                );

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id:.+}/view_count")
    public ResponseEntity<?> addBannerViewCount(@PathVariable Long id,
                                                @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
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
        private Date startedAt;
        private Date endedAt;
        private Long viewCount;
        private Date createdAt;
        private PostController.PostInfo post;

        public BannerInfo(Banner banner) {
            BeanUtils.copyProperties(banner, this);
        }

        public BannerInfo(Banner banner, PostController.PostInfo post) {
            this(banner);
            this.post = post;
        }

        public BannerInfo(Banner banner, PostController.PostInfo post, String slimThumbnailUrl) {
            this(banner, post);
            this.thumbnailUrl = slimThumbnailUrl;
        }
    }
}
