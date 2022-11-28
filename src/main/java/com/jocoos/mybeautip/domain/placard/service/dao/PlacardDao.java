package com.jocoos.mybeautip.domain.placard.service.dao;

import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PlacardDao {

    private final PlacardRepository repository;

    @Transactional
    public void save(Placard placard) {
        repository.save(placard);
    }

    @Transactional(readOnly = true)
    public Page<AdminPlacardResponse> getPlacards(PlacardSearchCondition condition) {
        return repository.getPlacards(condition);
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
    public void arrangeByIndex(List<Long> ids) {
        repository.arrangeByIndex(ids);
    }

    @Transactional
    public void unFixAndSortingToNull(Long placardId) {
        repository.unFixAndSortingToNull(placardId);
    }
}
