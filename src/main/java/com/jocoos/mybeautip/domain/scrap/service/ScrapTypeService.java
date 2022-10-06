package com.jocoos.mybeautip.domain.scrap.service;

import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;

import java.util.List;

public interface ScrapTypeService {

    List<CommunityScrapResponse> getScrapInfo(List<Scrap> scrapList);
}
