package com.jocoos.mybeautip.domain.scrap.service.impl;

import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.ScrapTypeService;
import com.jocoos.mybeautip.domain.vod.dto.VodListResponse;
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

    @Override
    public ScrapType getType() {
        return VOD;
    }

    @Override
    public List<MyScrapResponse<VodListResponse>> getScrapInfo(List<Scrap> scraps) {
        Map<Long, Scrap> vodIdScrapMap = MapUtil.toMap(scraps, Scrap::getRelationId);
        VodSearchCondition condition = VodSearchCondition.builder()
                .ids(vodIdScrapMap.keySet())
                .build();

        List<VodListResponse> responses = vodDao.getListWithMember(condition);
        return responses.stream()
                .map(response -> new MyScrapResponse<>(vodIdScrapMap.get(response.getId()).getId(), VOD, response))
                .toList();
    }
}
