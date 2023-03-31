package com.jocoos.mybeautip.domain.scrap.service;

import com.jocoos.mybeautip.domain.community.converter.CommunityScrapConverter;
import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.dao.ScrapDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final LegacyMemberService memberService;
    private final ScrapTypeFactory scrapTypeFactory;
    private final ScrapDao dao;
    private final CommunityScrapConverter converter;

    @Transactional
    public ScrapResponse scrap(ScrapRequest request) {
        long memberId = memberService.currentMemberId();
        request.setMemberId(memberId);
        Scrap scrap = dao.scrap(request);
        return converter.convert(scrap);
    }

    @Transactional(readOnly = true)
    public <T extends CursorInterface> List<MyScrapResponse<T>> getScrapList(ScrapType type,
                                                                             long cursor,
                                                                             Pageable pageable,
                                                                             Long memberId) {
        if (ScrapType.VIDEO.equals(type)) {
            throw new BadRequestException(type + " not supported yet. Use /api/1/members/me/scraps");
        }
        List<Scrap> scrapList = dao.getScraps(type, memberId, cursor, pageable);

        if (isEmpty(scrapList)) {
            return List.of();
        }
        ScrapTypeService<T> scrapTypeService = scrapTypeFactory.get(type);
        return scrapTypeService.getScrapInfo(scrapList);
    }

    @Transactional(readOnly = true)
    public boolean isScrapExist() {
        Member member = memberService.currentMember();
        return dao.isExist(member);
    }
}
