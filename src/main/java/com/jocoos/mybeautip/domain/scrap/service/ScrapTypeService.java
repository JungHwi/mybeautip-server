package com.jocoos.mybeautip.domain.scrap.service;

import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;

import java.util.List;

public interface ScrapTypeService {

    List<ScrapResponse> getScrapInfo(List<Scrap> scrapList);
}
