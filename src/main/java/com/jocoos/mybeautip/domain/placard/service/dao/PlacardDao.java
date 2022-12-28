package com.jocoos.mybeautip.domain.placard.service.dao;

import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.ACTIVE;
import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.INACTIVE;

@RequiredArgsConstructor
@Service
public class PlacardDao {

    private final PlacardRepository repository;

    @Transactional
    public Placard save(Placard placard) {
        return repository.save(placard);
    }

    @Transactional(readOnly = true)
    public Page<AdminPlacardResponse> getPlacards(PlacardSearchCondition condition) {
        return repository.getPlacardsWithCount(condition);
    }

    @Transactional(readOnly = true)
    public Placard getById(Long placardId) {
        return repository.findById(placardId)
                .orElseThrow(() -> new NotFoundException("placard not found " + placardId));
    }

    @Transactional
    public void delete(Long placardId) {
        repository.deleteById(placardId);
    }

    @Transactional
    public void fixAndAddToLastOrder(Long id) {
        repository.fixAndAddToLastOrder(id);
    }

    @Transactional
    public List<Long> arrangeByIndex(List<Long> ids) {
        return repository.arrangeByIndex(ids);
    }

    @Transactional
    public void unFixAndSortingToNull(Long placardId) {
        repository.unFixAndSortingToNull(placardId);
    }

    @Transactional(readOnly = true)
    public List<Placard> findStartPlacards() {
        return repository.findAllByStartedAtLessThanEqualAndEndedAtGreaterThanAndStatus(ZonedDateTime.now(), ZonedDateTime.now(), INACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Placard> findEndPlacards() {
        return repository.findAllByEndedAtLessThanEqualAndStatus(ZonedDateTime.now(), ACTIVE);
    }

    @Transactional
    public long updateStatus(List<Long> ids, PlacardStatus status) {
       return repository.updateStatus(ids, status);
    }
}
