package com.jocoos.mybeautip.domain.scrap.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.scrap.converter.ScrapConverter;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.ScrapTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityScrapService implements ScrapTypeService {
    private final CommunityDao communityDao;
    private final ScrapConverter converter;

    @Override
    public List<ScrapResponse> getScrapInfo(List<Scrap> scrapList) {
        List<Long> ids = scrapList.stream()
                .map(Scrap::getRelationId)
                .collect(Collectors.toList());

        List<Community> communityList = communityDao.get(ids);
        return converter.convertCommunityScrap(scrapList, communityList);
    }
}
