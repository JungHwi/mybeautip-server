package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoCategoryMapping;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.scrap.VideoScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.code.LikeStatus.LIKE;
import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;
import static com.jocoos.mybeautip.video.scrap.ScrapStatus.SCRAP;

@RequiredArgsConstructor
@Service
public class VideoConvertService {

    private final LegacyMemberService memberService;
    private final VideoCategoryService videoCategoryService;
    private final VideoLikeRepository videoLikeRepository;
    private final VideoScrapRepository videoScrapRepository;
    private final BlockRepository blockRepository;

    @Transactional(readOnly = true)
    public List<VideoResponse> toResponses(List<Video> videoList) {
        Map<Integer, VideoCategoryResponse> videoCategoryResponseMap = getVideoCategoryMap(videoList);
        List<VideoResponse> responseList = new ArrayList<>();
        videoList.forEach(v -> responseList.add(generateRelationInfo(v, videoCategoryResponseMap)));
        return responseList;
    }

    @Transactional(readOnly = true)
    public VideoResponse toResponse(Video video) {
        Map<Integer, VideoCategoryResponse> videoCategoryResponseMap = getVideoCategoryMap(video);
        return generateRelationInfo(video, videoCategoryResponseMap);
    }

    private VideoResponse generateRelationInfo(Video video, Map<Integer, VideoCategoryResponse> videoCategoryMap) {
        Long likeId = null;
        Long scrapId = null;
        boolean blocked = false;

        Long me = memberService.currentMemberId();
        // Set likeID
        if (me != null) {
            Optional<VideoLike> optional = videoLikeRepository.findByVideoIdAndCreatedByIdAndStatus(video.getId(), me, LIKE);
            likeId = optional.map(VideoLike::getId).orElse(null);
            scrapId = videoScrapRepository.findByVideoIdAndCreatedByIdAndStatus(video.getId(), me, SCRAP)
                    .map(s -> s.getId()).orElse(null);
            blocked = blockRepository.findByMeAndMemberYouIdAndStatus(video.getMember().getId(), me, BLOCK).isPresent();
        }

        VideoResponse videoResponse = new VideoResponse(video, memberService.getMemberInfo(video.getMember()), likeId, blocked);
        if (scrapId != null) {
            videoResponse.setScrapId(scrapId);
        }
        videoResponse.setWatchCount(video.getViewCount());
        videoResponse.setRealWatchCount(video.getWatchCount());
        List<VideoCategoryResponse> categoryResponses = new ArrayList<>();
        for (VideoCategoryMapping category : video.getCategory()) {
            categoryResponses.add(videoCategoryMap.get(category.getCategoryId()));
        }
        videoResponse.setCategory(categoryResponses);
        return videoResponse;
    }

    private Map<Integer, VideoCategoryResponse> getVideoCategoryMap(List<Video> videoList) {
        List<Integer> categoryIds = videoList.stream()
                .map(video -> video.getCategory())
                .collect(Collectors.toList())
                .stream().flatMap(List::stream)
                .map(category -> category.getCategoryId())
                .collect(Collectors.toList());

        List<VideoCategoryResponse> categoryResponses = videoCategoryService.getVideoCategoryList(categoryIds);
        return categoryResponses.stream()
                .collect(Collectors.toMap(VideoCategoryResponse::getId, Function.identity()));
    }

    private Map<Integer, VideoCategoryResponse> getVideoCategoryMap(Video video) {
        List<Integer> categoryIds = video.getCategory().stream()
                .map(category -> category.getCategoryId())
                .collect(Collectors.toList());

        List<VideoCategoryResponse> categoryResponses = videoCategoryService.getVideoCategoryList(categoryIds);
        return categoryResponses.stream()
                .collect(Collectors.toMap(VideoCategoryResponse::getId, Function.identity()));
    }
}
