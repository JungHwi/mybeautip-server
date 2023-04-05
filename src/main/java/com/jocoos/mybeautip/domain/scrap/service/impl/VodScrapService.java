package com.jocoos.mybeautip.domain.scrap.service.impl;

import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.ScrapTypeService;
import com.jocoos.mybeautip.domain.scrap.vo.ScrapTypeCondition;
import com.jocoos.mybeautip.domain.vod.dto.VodListResponse;
import com.jocoos.mybeautip.domain.vod.service.VodRelationService;
import com.jocoos.mybeautip.domain.vod.service.dao.VodDao;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.util.MapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.scrap.code.ScrapType.VOD;

@RequiredArgsConstructor
@Service
public class VodScrapService implements ScrapTypeService<VodListResponse> {

    private final VodDao vodDao;
    private final VodRelationService relationService;

    @Override
    public ScrapType getType() {
        return VOD;
    }

    @Override
    public List<MyScrapResponse<VodListResponse>> getScrapInfo(ScrapTypeCondition condition) {

        Map<Long, Scrap> vodIdScrapMap = MapUtil.toMap(condition.scraps(), Scrap::getRelationId);
        VodSearchCondition searchCondition = VodSearchCondition.builder()
                .ids(vodIdScrapMap.keySet())
                .build();

        List<VodListResponse> results = vodDao.getListWithMember(searchCondition);
        List<VodListResponse> responses = relationService.setRelationsAndGet(results, condition.memberId());

        return responses.stream()
                .map(response -> new MyScrapResponse<>(vodIdScrapMap.get(response.getId()).getId(), VOD, response))
                .toList();
    }
}
