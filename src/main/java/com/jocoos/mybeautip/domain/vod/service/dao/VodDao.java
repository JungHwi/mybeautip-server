package com.jocoos.mybeautip.domain.vod.service.dao;

import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.persistence.repository.VodRepository;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
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

    @Transactional
    public Vod save(Vod vod) {
        return repository.save(vod);
    }

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
    public Vod getByVideoKey(Long videoKey) {
        return repository.findByVideoKey(videoKey)
                .orElseThrow(() -> new NotFoundException("vod not found. video key - " + videoKey));
    }

    @Transactional(readOnly = true)
    public Vod getForUpdate(long vodId) {
        return repository.selectForUpdate(vodId).orElseThrow(vodNotFound(vodId));
    }

    @Transactional
    public void addReportCountAndFlush(long vodId, int count) {
        repository.addReportCount(vodId, count);
    }

    private Supplier<NotFoundException> vodNotFound(long vodId) {
        return () -> new NotFoundException("vod not found. id - " + vodId);
    }
}
