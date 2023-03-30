package com.jocoos.mybeautip.domain.scrap.service.impl;

import com.jocoos.mybeautip.domain.community.converter.CommunityScrapConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.CommunityRelationService;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.ScrapTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.scrap.code.ScrapType.COMMUNITY;

@Service
@RequiredArgsConstructor
public class CommunityScrapService implements ScrapTypeService<CommunityResponse> {

    private final CommunityRelationService relationService;
    private final CommunityDao communityDao;
    private final CommunityScrapConverter converter;

    @Override
    public ScrapType getType() {
        return COMMUNITY;
    }

    @Override
    public List<MyScrapResponse<CommunityResponse>> getScrapInfo(List<Scrap> scrapList) {
        List<Long> ids = scrapList.stream()
                .map(Scrap::getRelationId)
                .collect(Collectors.toList());

        List<Community> communityList = communityDao.get(ids);
        List<MyScrapResponse<CommunityResponse>> responseList = converter.convertCommunityScrap(scrapList, communityList);

        List<CommunityResponse> communityResponses = responseList.stream()
                .map(MyScrapResponse::getResponse)
                .collect(Collectors.toList());
        relationService.setRelationInfo(communityResponses);
        return responseList;
    }
}
