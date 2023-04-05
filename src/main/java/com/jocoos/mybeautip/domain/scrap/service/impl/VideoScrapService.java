package com.jocoos.mybeautip.domain.scrap.service.impl;

import com.jocoos.mybeautip.domain.community.converter.CommunityScrapConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.ScrapTypeService;
import com.jocoos.mybeautip.domain.scrap.vo.ScrapTypeCondition;
import com.jocoos.mybeautip.video.LegacyVideoService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoScrapService implements ScrapTypeService<CommunityResponse> {

    private final LegacyVideoService videoService;
    private final CommunityScrapConverter converter;

    @Override
    public ScrapType getType() {
        return ScrapType.VIDEO;
    }


    // FIXME: Make Support For Current Logic
    @Transactional(readOnly = true)
    public List<MyScrapResponse<CommunityResponse>> getScrapInfo(ScrapTypeCondition condition) {
        List<Scrap> scrapList = condition.scraps();
        List<Long> ids = scrapList.stream()
                .map(Scrap::getRelationId)
                .collect(Collectors.toList());

        List<Video> videoList = videoService.findByIdInAndVisibility(ids, Visibility.PUBLIC);
        return converter.convertVideoScrap(scrapList, videoList);
    }
}
