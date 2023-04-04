package com.jocoos.mybeautip.domain.vod.persistence.repository;

import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VodCustomRepository {
    Page<VodResponse> getPageList(VodSearchCondition condition);
    List<VodResponse> getVodResponses(VodSearchCondition condition);
    Page<Vod> getVodPage(VodSearchCondition condition);
    long count(VodSearchCondition condition);
}
