package com.jocoos.mybeautip.domain.vod.persistence.repository;

import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;

import java.util.List;

public interface VodCustomRepository {
    List<VodResponse> getVodListWithMember(VodSearchCondition condition);
    List<Vod> getVodList(VodSearchCondition condition);
}
