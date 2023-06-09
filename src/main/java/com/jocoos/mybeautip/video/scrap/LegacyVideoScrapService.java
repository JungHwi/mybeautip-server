package com.jocoos.mybeautip.video.scrap;

import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.domain.video.service.VideoConvertService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.support.DateUtils;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.Visibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.CANCEL_VIDEO_SCRAP;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.VIDEO_SCRAP;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainAndReceiver;
import static com.jocoos.mybeautip.video.scrap.ScrapStatus.SCRAP;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyVideoScrapService {

    private final VideoRepository videoRepository;
    private final VideoScrapRepository videoScrapRepository;
    private final ActivityPointService activityPointService;
    private final VideoConvertService convertService;

    @Transactional
    public VideoScrap scrapVideo(Video video, Member member) {
        if (videoScrapRepository.existsByVideoIdAndCreatedByIdAndStatus(video.getId(), member.getId(), SCRAP)) {
            throw new BadRequestException("already_scrap");
        }
        VideoScrap videoScrap = saveScrapVideo(video, member);
        activityPointService.gainActivityPoint(VIDEO_SCRAP, validDomainAndReceiver(video, videoScrap.getId(), member));
        return videoScrap;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public VideoScrap saveScrapVideo(Video video, Member member) {
        video.setScrapCount(video.getScrapCount() + 1);
        videoRepository.save(video);

        VideoScrap videoScrap = videoScrapRepository.findByVideoIdAndCreatedById(video.getId(), member.getId())
                .orElse(new VideoScrap(video));
        videoScrap.scrap();
        return videoScrapRepository.save(videoScrap);
    }

    @Transactional
    public void deleteScrap(Video video, Member member) {
        VideoScrap scrap = videoScrapRepository.findByVideoIdAndCreatedById(video.getId(), member.getId())
                .orElseThrow(() -> new NotFoundException("Scrap not found. videoId -" + video.getId() + ", memberId - " + member.getId()));

        scrap.notScrap();
        video.setScrapCount(video.getScrapCount() - 1);
        videoRepository.save(video);
        activityPointService.retrieveActivityPoint(CANCEL_VIDEO_SCRAP, scrap.getId(), member);
    }

    public List<VideoScrap> findByMemberId(Long memberId, String cursor, Visibility visibility, Pageable pageable) {
        String visibilityName = visibility != null ? visibility.name() : Visibility.PUBLIC.name();
        if (!StringUtils.isBlank(cursor)) {
            Date createdAtBefore = DateUtils.toDate(cursor);
            log.debug("cursor: {}", createdAtBefore);
            return videoScrapRepository.findByCreatedByIdAndCreatedAtBeforeAndVideoVisibilityAndVideoDeletedAtIsNullAndStatus(memberId, createdAtBefore, visibilityName, pageable, SCRAP);
        }
        return videoScrapRepository.findByCreatedByIdAndVideoVisibilityAndVideoDeletedAtIsNullAndStatus(memberId, visibilityName, pageable, SCRAP);
    }
}
