package com.jocoos.mybeautip.domain.scrap.service;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.converter.ScrapConverter;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.dao.ScrapDao;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final LegacyMemberService memberService;
    private final ScrapTypeFactory scrapTypeFactory;
    private final ScrapDao dao;
    private final ScrapConverter converter;

    @Transactional
    public ScrapResponse scrap(ScrapRequest request) {
        long memberId = memberService.currentMemberId();
        request.setMemberId(memberId);

        Scrap scrap = dao.scrap(request);
        return converter.convert(scrap);
    }

    @Transactional(readOnly = true)
    public List<ScrapResponse> getScrapList(ScrapType type, long cursor, Pageable pageable) {
        List<Scrap> scrapList = dao.getScrapList(type, cursor, pageable);

        ScrapTypeService scrapTypeService = scrapTypeFactory.getScrapTypeService(type);

        return scrapTypeService.getScrapInfo(scrapList);
    }

}
