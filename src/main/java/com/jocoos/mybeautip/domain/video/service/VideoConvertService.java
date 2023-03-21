package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.converter.VideoConverter;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoCategoryMapping;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoLikeRepository;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.video.report.VideoReportRepository;
import com.jocoos.mybeautip.video.scrap.VideoScrap;
import com.jocoos.mybeautip.video.scrap.VideoScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final VideoReportRepository videoReportRepository;
    private final BlockRepository blockRepository;
    private final VideoConverter converter;

    @Transactional(readOnly = true)
    public List<VideoResponse> toResponses(List<Video> videoList) {
        Map<Integer, VideoCategoryResponse> videoCategoryResponseMap = getVideoCategoryMap(videoList);
        return generateRelationInfo(videoList, videoCategoryResponseMap);
    }

    @Transactional(readOnly = true)
    public VideoResponse toResponse(Video video) {
        Map<Integer, VideoCategoryResponse> videoCategoryResponseMap = getVideoCategoryMap(video);
        return generateRelationInfo(video, videoCategoryResponseMap);
    }

    private List<VideoResponse> generateRelationInfo(List<Video> videoList, Map<Integer, VideoCategoryResponse> videoCategoryMap) {
        List<VideoResponse> responseList = new ArrayList<>();
        Map<Long, Long> likeMap = new HashMap<>();
        Map<Long, Long> scrapMap = new HashMap<>();
        Map<Long, Block> blockMap = new HashMap<>();
        Map<Long, Long> reportMap = new HashMap<>();

        Long me = memberService.currentMemberId();

        List<Long> videoIds = videoList.stream()
                .map(Video::getId)
                .toList();

        List<Long> ownerIds = videoList.stream()
                .map(video -> video.getMember().getId())
                .toList();

        if (me != null) {
            List<VideoLike> likeList = videoLikeRepository.findByVideoIdInAndCreatedByIdAndStatus(videoIds, me, LIKE);
            likeMap = likeList.stream()
                    .collect(Collectors.toMap(videoLike -> videoLike.getVideo().getId(), VideoLike::getId));
            List<VideoScrap> scrapList = videoScrapRepository.findByVideoIdInAndCreatedByIdAndStatus(videoIds, me, SCRAP);
            scrapMap = scrapList.stream()
                    .collect(Collectors.toMap(videoScrap -> videoScrap.getVideo().getId(), VideoScrap::getId));
            List<Block> blockList = blockRepository.findAllByMeAndMemberYouIdInAndStatus(me, ownerIds, BLOCK);
            blockMap = blockList.stream()
                    .collect(Collectors.toMap(Block::getYouId, Function.identity()));
            List<VideoReport> reportList = videoReportRepository.findByVideoIdInAndCreatedById(videoIds, me);
            reportMap = reportList.stream().collect(Collectors.toMap(videoReport -> videoReport.getVideo().getId(), VideoReport::getId));
        }

        for (Video video : videoList) {
            VideoResponse response = converter.converts(video);
            response.setWatchCount(video.getViewCount());
            response.setRealWatchCount(video.getWatchCount());
            response.setLikeId(likeMap.getOrDefault(video.getId(), null));
            response.setScrapId(scrapMap.getOrDefault(video.getId(), null));
            response.setReportId(reportMap.getOrDefault(video.getId(), null));
            response.setBlocked(blockMap.containsKey(video.getId()));

            List<VideoCategoryResponse> categoryResponses = new ArrayList<>();
            for (VideoCategoryMapping category : video.getCategoryMapping()) {
                categoryResponses.add(videoCategoryMap.get(category.getVideoCategory().getId()));
            }
            response.setCategory(categoryResponses);

            responseList.add(response);
        }

        return responseList;
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
        for (VideoCategoryMapping category : video.getCategoryMapping()) {
            categoryResponses.add(videoCategoryMap.get(category.getVideoCategory().getId()));
        }
        videoResponse.setCategory(categoryResponses);
        return videoResponse;
    }

    private Map<Integer, VideoCategoryResponse> getVideoCategoryMap(List<Video> videoList) {
        List<Integer> categoryIds = videoList.stream()
                .map(video -> video.getCategoryMapping())
                .collect(Collectors.toList()).stream()
                .flatMap(List::stream)
                .map(mapping -> mapping.getVideoCategory().getId())
                .toList();

        List<VideoCategoryResponse> categoryResponses = videoCategoryService.getVideoCategoryList(categoryIds);
        return categoryResponses.stream()
                .collect(Collectors.toMap(VideoCategoryResponse::getId, Function.identity()));
    }

    private Map<Integer, VideoCategoryResponse> getVideoCategoryMap(Video video) {
        List<Integer> categoryIds = video.getCategoryMapping().stream()
                .map(category -> category.getVideoCategory().getId())
                .collect(Collectors.toList());

        List<VideoCategoryResponse> categoryResponses = videoCategoryService.getVideoCategoryList(categoryIds);
        return categoryResponses.stream()
                .collect(Collectors.toMap(VideoCategoryResponse::getId, Function.identity()));
    }
}
