package com.jocoos.mybeautip.domain.placard.persistence.repository;

import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlacardCustomRepository {
    Page<AdminPlacardResponse> getPlacardsWithCount(PlacardSearchCondition condition);

    List<Placard> getPlacards(PlacardSearchCondition condition);

    void fixAndAddToLastOrder(Long id);
    List<Long> arrangeByIndex(List<Long> ids);

    void unFixAndSortingToNull(Long placardId);

    long updateStatus(List<Long> ids, PlacardStatus status);
}
