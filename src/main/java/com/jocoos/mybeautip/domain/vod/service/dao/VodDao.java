package com.jocoos.mybeautip.domain.vod.service.dao;

import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.persistence.repository.VodRepository;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class VodDao {

    private final VodRepository repository;

    @Transactional
    public Vod save(Vod vod) {
        return repository.save(vod);
    }

    @Transactional(readOnly = true)
    public Page<VodResponse> getPageList(VodSearchCondition condition) {
        return repository.getPageList(condition);
    }

    @Transactional(readOnly = true)
    public List<VodResponse> getListWithMember(VodSearchCondition condition) {
        return repository.getVodResponses(condition);
    }

    @Transactional(readOnly = true)
    public Page<Vod> getList(VodSearchCondition condition) {
        return repository.getVodPage(condition);
    }

    @Transactional(readOnly = true)
    public Vod get(long vodId) {
        return repository.findById(vodId).orElseThrow(vodNotFound(vodId));
    }

    @Transactional(readOnly = true)
    public Vod getForUpdate(long vodId) {
        return repository.selectForUpdate(vodId).orElseThrow(vodNotFound(vodId));
    }

    @Transactional(readOnly = true)
    public long count(VodSearchCondition condition) {
        return repository.count(condition);
    }

    @Transactional
    public void addReportCountAndFlush(long vodId, int count) {
        repository.addReportCount(vodId, count);
    }

    private Supplier<NotFoundException> vodNotFound(long vodId) {
        return () -> new NotFoundException("vod not found. id - " + vodId);
    }
}
