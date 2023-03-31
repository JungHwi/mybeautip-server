package com.jocoos.mybeautip.domain.scrap.service;

import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;

import java.util.List;

public interface ScrapTypeService<T extends CursorInterface> {
    ScrapType getType();
    List<MyScrapResponse<T>> getScrapInfo(List<Scrap> scraps);
}
