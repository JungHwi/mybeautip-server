package com.jocoos.mybeautip.domain.vod.service;

import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.dao.ScrapDao;
import com.jocoos.mybeautip.domain.vod.dto.VodRelationInfo;
import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.global.util.EntityUtil;
import com.jocoos.mybeautip.global.util.MapUtil;
import com.jocoos.mybeautip.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jocoos.mybeautip.domain.scrap.code.ScrapType.VOD;

@RequiredArgsConstructor
@Service
public class VodRelationService {

    private final ScrapDao scrapDao;

    @Transactional(readOnly = true)
    public Map<Long, VodRelationInfo> getRelationInfoMap(String tokenUsername, List<VodResponse> results) {
        Set<Long> ids = EntityUtil.extractLongSet(results, VodResponse::getId);

        if (MemberUtil.isGuest(tokenUsername)) {
            return MapUtil.toIdentityMap(ids, id -> new VodRelationInfo(false));
        }

        List<Scrap> scraps = scrapDao.getScraps(VOD, Long.parseLong(tokenUsername));
        Set<Long> scrapVodIdList = EntityUtil.extractLongSet(scraps, Scrap::getRelationId);
        return MapUtil.toIdentityMap(ids, id -> new VodRelationInfo(scrapVodIdList.contains(id)));
    }
}
