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

@RequiredArgsConstructor
@Service
public class VodDao {

    private final VodRepository repository;

    @Transactional(readOnly = true)
    public List<VodResponse> getList(VodSearchCondition condition) {
        return repository.getVodList(condition);
    }

    @Transactional(readOnly = true)
    public Vod get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("vod not found. id - " + id));
    }
}
