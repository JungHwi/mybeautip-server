package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.recommendation.MotdRecommendation;
import com.jocoos.mybeautip.recommendation.MotdRecommendationRepository;
import com.jocoos.mybeautip.video.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/videos", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoRelationController {

    private static final String VIDEO_NOT_FOUND = "video.not_found";
    private final VideoService videoService;
    private final MessageService messageService;
    private final VideoRepository videoRepository;
    private final VideoGoodsRepository videoGoodsRepository;
    private final MotdRecommendationRepository motdRecommendationRepository;

    public VideoRelationController(VideoService videoService,
                                   MessageService messageService,
                                   VideoRepository videoRepository,
                                   VideoGoodsRepository videoGoodsRepository,
                                   MotdRecommendationRepository motdRecommendationRepository) {
        this.videoService = videoService;
        this.messageService = messageService;
        this.videoRepository = videoRepository;
        this.videoGoodsRepository = videoGoodsRepository;
        this.motdRecommendationRepository = motdRecommendationRepository;
    }


    @GetMapping("/{id:.+}/relations")
    public CursorResponse getRelationVideos(@PathVariable Long id,
                                            @RequestParam(defaultValue = "10") int count,
                                            @RequestParam(required = false) String cursor,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("video_not_found", messageService.getMessage(VIDEO_NOT_FOUND, lang)));

        PageRequest page = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<VideoGoods> videoGoods = videoGoodsRepository.findAllByVideoId(video.getId());

        Date dateCursor;
        if (cursor != null) {
            dateCursor = new Date(Long.parseLong(cursor));
        } else {
            dateCursor = new Date();
        }

        List<Goods> goodses = videoGoods.stream().map(VideoGoods::getGoods).collect(Collectors.toList());
        Set<Video> combines = new HashSet<>();
        for (Goods g : goodses) {
            log.debug("goods: {}", g.getGoodsNo());
            Slice<VideoGoods> goodsVideo = videoGoodsRepository.findByCreatedAtBeforeAndGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(dateCursor, g.getGoodsNo(), "PUBLIC", "CREATED", page);
            goodsVideo.stream().forEach(v -> {
                log.debug("v id: {}, v key: {}", v.getVideo().getId(), v.getVideo().getVideoKey());
                if (!v.getVideo().getId().equals(id)) {
                    combines.add(v.getVideo());
                }
            });

            log.debug("combines size: {}", combines.size());
            if (combines.size() == count) {
                return createResponse(new ArrayList<>(combines), id, count, cursor);
            }
        }

        Slice<Video> userVideos = videoRepository.getUserAllVideos(video.getMember(), dateCursor, page);

        userVideos.stream().forEach(v -> {
            log.debug("v id: {}, v key: {}", v.getId(), v.getVideoKey());
            if (combines.size() < count) {
                if (!v.getId().equals(id)) {
                    combines.add(v);
                }
            }
        });

        if (combines.size() == count) {
            return createResponse(new ArrayList<>(combines), id, count, cursor);
        }

        Slice<MotdRecommendation> recommendations = motdRecommendationRepository.findByVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNotAndCreatedAtBefore("PUBLIC", "CREATED", dateCursor, page);
        recommendations.stream().forEach(r -> {
            if (combines.size() < count) {
                if (!r.getVideo().getId().equals(id)) {
                    combines.add(r.getVideo());
                }
            }
        });

        return createResponse(new ArrayList<>(combines), id, count, cursor);
    }

    private CursorResponse createResponse(List<Video> list, Long id, int count, String cursor) {
        List<VideoController.VideoInfo> videos = new ArrayList<>();
        Collections.sort(list, (Video o1, Video o2) -> o2.getId().compareTo(o1.getId()));

        list.stream().forEach(v -> videos.add(videoService.generateVideoInfo(v)));

        String nextCursor = null;
        if (videos.size() > 0) {
            nextCursor = String.valueOf(videos.get(videos.size() - 1).getCreatedAt().getTime());
        }

        return new CursorResponse.Builder<>(String.format("/api/1/videos/%s/relations", id), videos)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }
}
