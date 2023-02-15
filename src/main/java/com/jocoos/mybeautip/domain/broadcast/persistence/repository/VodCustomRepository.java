package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;

import java.util.List;

public interface VodCustomRepository {
    List<VodResponse> getVodList(VodSearchCondition condition);
}
