package com.jocoos.mybeautip.domain.vod.service;

import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.dao.ScrapDao;
import com.jocoos.mybeautip.domain.vod.dto.VodListResponse;
import com.jocoos.mybeautip.domain.vod.dto.VodRelationInfo;
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


    // If Need Immutable, Change This Method To Make Another List
    @Transactional(readOnly = true)
    public List<VodListResponse> setRelationsAndGet(List<VodListResponse> results, String tokenUsername) {
        Set<Long> ids = EntityUtil.extractLongSet(results, VodListResponse::getId);
        Map<Long, VodRelationInfo> relationInfoMap = getRelationInfoMap(tokenUsername, ids);
        results.forEach(response -> response.setRelationInfo(relationInfoMap.get(response.getId())));
        return results;
    }

    @Transactional(readOnly = true)
    public List<VodListResponse> setRelationsAndGet(List<VodListResponse> results, Long memberId) {
        Set<Long> ids = EntityUtil.extractLongSet(results, VodListResponse::getId);
        Map<Long, VodRelationInfo> relationInfoMap = getRelationInfoMapForMember(memberId, ids);
        results.forEach(response -> response.setRelationInfo(relationInfoMap.get(response.getId())));
        return results;
    }

    @Transactional(readOnly = true)
    public VodRelationInfo getRelationInfo(String tokenUsername, Long vodId) {
        if (MemberUtil.isGuest(tokenUsername)) {
            return new VodRelationInfo(false);
        }

        boolean isScrap = scrapDao.isScrap(VOD, Long.parseLong(tokenUsername), vodId);
        return new VodRelationInfo(isScrap);
    }

    private Map<Long, VodRelationInfo> getRelationInfoMap(String tokenUsername, Set<Long> ids) {
        if (MemberUtil.isGuest(tokenUsername)) {
            return MapUtil.toIdentityMap(ids, id -> new VodRelationInfo(false));
        }
        return getRelationInfoMapForMember(Long.parseLong(tokenUsername), ids);
    }

    private Map<Long, VodRelationInfo> getRelationInfoMapForMember(Long memberId, Set<Long> ids) {
        List<Scrap> scraps = scrapDao.getScraps(VOD, memberId);
        Set<Long> scrapVodIdList = EntityUtil.extractLongSet(scraps, Scrap::getRelationId);
        return MapUtil.toIdentityMap(ids, id -> new VodRelationInfo(scrapVodIdList.contains(id)));
    }
}
