package com.jocoos.mybeautip.domain.placard.persistence.repository;

import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlacardCustomRepository {
    Page<AdminPlacardResponse> getPlacards(PlacardSearchCondition condition);
    void fixAndAddToLastOrder(Long id);
    List<Long> arrangeByIndex(List<Long> ids);

    void unFixAndSortingToNull(Long placardId);
}
