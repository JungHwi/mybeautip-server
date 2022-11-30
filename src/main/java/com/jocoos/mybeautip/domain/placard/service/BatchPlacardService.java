package com.jocoos.mybeautip.domain.placard.service;

import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.dto.BatchPlacardResult;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.service.dao.PlacardDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.ACTIVE;
import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.INACTIVE;

@RequiredArgsConstructor
@Service
public class BatchPlacardService {

    private final PlacardDao placardDao;


    @Transactional
    public BatchPlacardResult changeStatus() {
        long activePlacardCount = changeStatus(placardDao.findStartPlacards(), ACTIVE);
        long inActivePlacardCount = changeStatus(placardDao.findEndPlacards(), INACTIVE);
        return new BatchPlacardResult(activePlacardCount, inActivePlacardCount);
    }

    private long changeStatus(List<Placard> placards, PlacardStatus status) {
        List<Long> ids = placards.stream().map(Placard::getId).toList();
        return placardDao.updateStatus(ids, status);
    }
}
