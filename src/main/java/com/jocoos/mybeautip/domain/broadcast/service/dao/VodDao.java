package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.VodRepository;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class VodDao {

    private final VodRepository repository;

    @Transactional(readOnly = true)
    public List<VodResponse> getListWithMember(VodSearchCondition condition) {
        return repository.getVodListWithMember(condition);
    }

    @Transactional(readOnly = true)
    public List<Vod> getList(VodSearchCondition condition) {
        return repository.getVodList(condition);
    }

    @Transactional(readOnly = true)
    public Vod get(long vodId) {
        return repository.findById(vodId).orElseThrow(vodNotFound(vodId));
    }

    @Transactional(readOnly = true)
    public Vod getForUpdate(long vodId) {
        return repository.selectForUpdate(vodId).orElseThrow(vodNotFound(vodId));
    }

    private Supplier<NotFoundException> vodNotFound(long vodId) {
        return () -> new NotFoundException("vod not found. id - " + vodId);
    }
}
