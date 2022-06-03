package com.jocoos.mybeautip.recommendation;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.restapi.CursorResponse;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.tag.Tag;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/1/recommendations")
public class RecommendationController {

    private static final String RESOURCE_TYPE_MEMBER = "member";
    private static final String RESOURCE_TYPE_TAG = "tag";
    private static final String DEFAULT_HASHTAG_IMG_URL = "https://mybeautip.s3.ap-northeast-2.amazonaws.com/app/img_hashtag_default.png";
    private final GoodsService goodsService;
    private final LegacyMemberService legacyMemberService;
    private final VideoService videoService;
    private final VideoRepository videoRepository;
    private final MemberRecommendationRepository memberRecommendationRepository;
    private final GoodsRecommendationRepository goodsRecommendationRepository;
    private final MotdRecommendationRepository motdRecommendationRepository;
    private final MotdRecommendationBaseRepository motdRecommendationBaseRepository;
    private final KeywordRecommendationRepository keywordRecommendationRepository;
    private final int MAX_RECOMMENDED_BJ_COUNT = 100;
    private final int MAX_RECOMMENDED_KEYWORD_COUNT = 100;

    public RecommendationController(GoodsService goodsService,
                                    LegacyMemberService legacyMemberService,
                                    VideoService videoService,
                                    VideoRepository videoRepository,
                                    MemberRecommendationRepository memberRecommendationRepository,
                                    GoodsRecommendationRepository goodsRecommendationRepository,
                                    MotdRecommendationRepository motdRecommendationRepository,
                                    MotdRecommendationBaseRepository motdRecommendationBaseRepository,
                                    KeywordRecommendationRepository keywordRecommendationRepository) {
        this.goodsService = goodsService;
        this.legacyMemberService = legacyMemberService;
        this.videoService = videoService;
        this.videoRepository = videoRepository;
        this.memberRecommendationRepository = memberRecommendationRepository;
        this.goodsRecommendationRepository = goodsRecommendationRepository;
        this.motdRecommendationRepository = motdRecommendationRepository;
        this.motdRecommendationBaseRepository = motdRecommendationBaseRepository;
        this.keywordRecommendationRepository = keywordRecommendationRepository;
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberInfo>> getRecommendedMembers(
            @RequestParam(defaultValue = "100") int count) {
        Date now = new Date();
        List<MemberRecommendation> members = memberRecommendationRepository.findByStartedAtBeforeAndEndedAtAfterAndMemberVisibleIsTrueAndSeqLessThan(
                now, now, MAX_RECOMMENDED_BJ_COUNT + 1, PageRequest.of(0, count, Sort.by("seq").ascending()));
        List<MemberInfo> result = new ArrayList<>();

        members.forEach(r -> {
            MemberInfo memberInfo = legacyMemberService.getMemberInfo(r.getMember());
            if (memberInfo.getVideoCount() > 0) {
                List<VideoController.VideoInfo> videoList = new ArrayList<>();
                Slice<Video> slice = videoRepository.getUserAllVideos(r.getMember(), new Date(), PageRequest.of(0, 3));
                if (slice.hasContent()) {
                    for (Video video : slice) {
                        videoList.add(videoService.generateVideoInfo(video));
                    }
                    memberInfo.setVideos(videoList);
                }
                result.add(memberInfo);
            }
        });

        count = count - result.size();
        if (count > 0) {
            members = memberRecommendationRepository.findByStartedAtBeforeAndEndedAtAfterAndMemberVisibleIsTrueAndSeqGreaterThan(
                    now, now, MAX_RECOMMENDED_BJ_COUNT, PageRequest.of(0, count, Sort.by("seq")));
            Collections.shuffle(members);

            members.forEach(r -> {
                MemberInfo memberInfo = legacyMemberService.getMemberInfo(r.getMember());
                if (memberInfo.getVideoCount() > 0) {
                    List<VideoController.VideoInfo> videoList = new ArrayList<>();
                    Slice<Video> slice = videoRepository.getUserAllVideos(r.getMember(), new Date(), PageRequest.of(0, 3));
                    if (slice.hasContent()) {
                        for (Video video : slice) {
                            videoList.add(videoService.generateVideoInfo(video));
                        }
                        memberInfo.setVideos(videoList);
                    }
                    result.add(memberInfo);
                }
            });
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/goods")
    public ResponseEntity<List<GoodsInfo>> getRecommendedGoods(@RequestParam(defaultValue = "100") int count) {
        PageRequest page = PageRequest.of(0, count, Sort.by("seq"));
        Date now = new Date();
        Slice<GoodsRecommendation> goods = goodsRecommendationRepository.findByStartedAtBeforeAndEndedAtAfterAndGoodsStateLessThanEqual(
                now, now, Goods.GoodsState.NO_SALE.ordinal(), page);

        List<GoodsInfo> result = new ArrayList<>();
        goods.stream().forEach(recommendation
                -> result.add(goodsService.generateGoodsInfo(recommendation.getGoods())));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/live")
    public ResponseEntity<List<VideoController.VideoInfo>> getRecommendedLiveVideos() {
        // response up to 10 live videos regardless of member
        Slice<Video> list = videoService.findVideos("BROADCASTED", "LIVE", null, 10);
        List<VideoController.VideoInfo> result = new ArrayList<>();
        list.stream().forEach(v -> result.add(videoService.generateVideoInfo(v)));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private List<RecommendedMotdInfo> createMotdList(Iterable<MotdRecommendation> recommendations) {
        List<RecommendedMotdInfo> info = new ArrayList<>();
        for (MotdRecommendation recommendation : recommendations) {
            info.add(new RecommendedMotdInfo(recommendation, videoService.generateVideoInfo(recommendation.getVideo())));
        }
        return info;
    }

    @GetMapping("/motd-bases")
    public CursorResponse getBaseRecommendedMotds(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Long cursor) {

        Date createDate = null;
        if (cursor != null) {
            createDate = new Date(cursor);
        } else {
            createDate = new Date();
        }

        Slice<MotdRecommendationBase> videos = motdRecommendationBaseRepository.findByBaseDateBefore(createDate,
                PageRequest.of(0, count, Sort.by(Sort.Direction.fromString(direction), "baseDate")));

        Date now = new Date();
        List<RecommendedMotdBaseInfo> result = videos.stream()
                .map(base -> {
                    Slice<MotdRecommendation> motds = motdRecommendationRepository
                            .findByBaseIdAndStartedAtBeforeAndEndedAtAfterAndVideoVisibilityAndVideoDeletedAtIsNull(
                                    base.getId(), now, now, "PUBLIC",
                                    PageRequest.of(0, base.getMotdCount(), Sort.Direction.DESC, "seq", "createdAt"));
                    return new RecommendedMotdBaseInfo(base, createMotdList(motds));
                })
                .filter(i -> i.getExposedCount() > 0)
                .collect(Collectors.toList());

        String nextCursor = null;
        if (!CollectionUtils.isEmpty(result)) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getBaseDate().getTime());
        }

        return new CursorResponse.Builder<>("/api/1/recommendations/motd-bases", result)
                .withCount(count)
                .withCursor(nextCursor)
                .toBuild();
    }

    @Deprecated
    @GetMapping("/keywords")
    public ResponseEntity<List<KeywordInfo>> getRecommendedKeywords(
            @RequestParam(defaultValue = "100") int count) {
        List<KeywordRecommendation> keywords = keywordRecommendationRepository.findBySeqLessThan(
                MAX_RECOMMENDED_KEYWORD_COUNT + 1, PageRequest.of(0, count, Sort.by(Sort.Direction.ASC, "seq")));

        List<KeywordInfo> result = new ArrayList<>();
        for (KeywordRecommendation keyword : keywords) {
            switch (keyword.getCategory()) {
                case 1:
                    if (keyword.getMember().isVisible()) {
                        result.add(new KeywordInfo(keyword, legacyMemberService.getMemberInfo(keyword.getMember())));
                    }
                    break;
                case 2:
                default:
                    result.add(new KeywordInfo(keyword, new TagInfo(keyword.getTag())));
                    break;
            }
        }

        count = count - result.size();
        if (count > 0) {
            keywords = keywordRecommendationRepository.findBySeqGreaterThan(
                    MAX_RECOMMENDED_KEYWORD_COUNT, PageRequest.of(0, MAX_RECOMMENDED_KEYWORD_COUNT, Sort.by(Sort.Direction.ASC, "seq")));
            Collections.shuffle(keywords);
            int subListCount = (count <= keywords.size()) ? count : keywords.size();
            List<KeywordRecommendation> subList = keywords.subList(0, subListCount);

            for (KeywordRecommendation keyword : subList) {
                switch (keyword.getCategory()) {
                    case 1:
                        if (keyword.getMember().isVisible()) {
                            result.add(new KeywordInfo(keyword, legacyMemberService.getMemberInfo(keyword.getMember())));
                        }
                        break;
                    case 2:
                    default:
                        result.add(new KeywordInfo(keyword, new TagInfo(keyword.getTag())));
                        break;
                }
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search-keywords")
    public ResponseEntity<List<SearchKeywordInfo>> getRecommendedSearchKeywords(
            @RequestParam(defaultValue = "100") int count) {
        List<KeywordRecommendation> keywords = keywordRecommendationRepository.findBySeqLessThan(
                MAX_RECOMMENDED_KEYWORD_COUNT + 1, PageRequest.of(0, count, Sort.by(Sort.Direction.ASC, "seq")));

        List<SearchKeywordInfo> result = new ArrayList<>();
        for (KeywordRecommendation keyword : keywords) {
            switch (keyword.getCategory()) {
                case 1:
                    if (keyword.getMember().isVisible()) {
                        result.add(new SearchKeywordInfo(keyword, legacyMemberService.getMemberInfo(keyword.getMember())));
                    }
                    break;
                case 2:
                default:
                    result.add(new SearchKeywordInfo(keyword, new TagInfo(keyword.getTag())));
                    break;
            }
        }

        count = count - result.size();
        if (count > 0) {
            keywords = keywordRecommendationRepository.findBySeqGreaterThan(
                    MAX_RECOMMENDED_KEYWORD_COUNT, PageRequest.of(0, MAX_RECOMMENDED_KEYWORD_COUNT, Sort.by(Sort.Direction.ASC, "seq")));
            Collections.shuffle(keywords);
            List<KeywordRecommendation> subList = keywords.subList(0, count);

            for (KeywordRecommendation keyword : subList) {
                switch (keyword.getCategory()) {
                    case 1:
                        if (keyword.getMember().isVisible()) {
                            result.add(new SearchKeywordInfo(keyword, legacyMemberService.getMemberInfo(keyword.getMember())));
                        }
                        break;
                    case 2:
                    default:
                        result.add(new SearchKeywordInfo(keyword, new TagInfo(keyword.getTag())));
                        break;
                }
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Data
    public static class KeywordInfo { // FIXME: will be deprecated
        Integer category;
        MemberInfo member;
        TagInfo tag;
        Integer seq;
        @Deprecated
        Date startedAt;
        @Deprecated
        Date endedAt;
        Date createdAt;
        Date modifiedAt;

        public KeywordInfo(KeywordRecommendation keyword, MemberInfo member) {
            BeanUtils.copyProperties(keyword, this);
            this.member = member;
            this.startedAt = new Date(System.currentTimeMillis());
            this.endedAt = new Date(System.currentTimeMillis());
        }

        public KeywordInfo(KeywordRecommendation keyword, TagInfo tag) {
            BeanUtils.copyProperties(keyword, this);
            this.tag = tag;
            this.startedAt = new Date(System.currentTimeMillis());
            this.endedAt = new Date(System.currentTimeMillis());
        }
    }

    @Data
    public static class SearchKeywordInfo {
        private Long id;
        private String resourceType;  // "member", "tag"
        private Long resourceId;
        private String imageUrl;
        private String title;
        private String content;
        private Date createdAt;

        public SearchKeywordInfo(KeywordRecommendation keyword, MemberInfo member) {
            id = keyword.getId();
            createdAt = keyword.getCreatedAt();
            resourceType = RESOURCE_TYPE_MEMBER;
            resourceId = member.getId();
            imageUrl = member.getAvatarUrl();
            title = member.getUsername();
        }

        public SearchKeywordInfo(KeywordRecommendation keyword, TagInfo tag) {
            id = keyword.getId();
            createdAt = keyword.getCreatedAt();
            resourceType = RESOURCE_TYPE_TAG;
            resourceId = tag.getId();
            imageUrl = DEFAULT_HASHTAG_IMG_URL;
            title = tag.getName();
            content = String.valueOf(tag.getRefCount());
        }
    }

    @Data
    public static class TagInfo {
        private Long id;
        private String name;
        private Integer refCount;

        public TagInfo(Tag tag) {
            BeanUtils.copyProperties(tag, this);
        }
    }

    @Data
    public static class RecommendedMotdInfo {
        private Integer seq;
        private Date createdAt;
        private VideoController.VideoInfo content;
        private Date startedAt;
        private Date endedAt;

        public RecommendedMotdInfo(MotdRecommendation recommendation, VideoController.VideoInfo content) {
            BeanUtils.copyProperties(recommendation, this);
            this.content = content;
        }
    }

    @Data
    public static class RecommendedMotdBaseInfo {
        private Long id;
        private Date baseDate;
        private List<RecommendedMotdInfo> motds;
        private Date createdAt;
        private int motdCount;
        private int exposedCount = 0;

        public RecommendedMotdBaseInfo(MotdRecommendationBase motdRecommendationBase) {
            BeanUtils.copyProperties(motdRecommendationBase, this);
        }

        public RecommendedMotdBaseInfo(MotdRecommendationBase motdRecommendationBase, List<RecommendedMotdInfo> motds) {
            this(motdRecommendationBase);
            this.motds = motds;
            if (!CollectionUtils.isEmpty(motds)) {
                this.exposedCount = motds.size();
            }
        }
    }
}