package com.jocoos.mybeautip.domain.scrap.service.impl;

import com.jocoos.mybeautip.domain.community.converter.CommunityScrapConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.ScrapTypeService;
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
public class VideoScrapService implements ScrapTypeService {

    private final LegacyVideoService videoService;
    private final CommunityScrapConverter converter;

    @Override
    @Transactional(readOnly = true)
    public List<CommunityScrapResponse> getScrapInfo(List<Scrap> scrapList) {
        List<Long> ids = scrapList.stream()
                .map(Scrap::getRelationId)
                .collect(Collectors.toList());

        List<Video> videoList = videoService.findByIdInAndVisibility(ids, Visibility.PUBLIC);
        return converter.convertVideoScrap(scrapList, videoList);
    }
}
