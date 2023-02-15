package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.VodRepository;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VodDao {

    private final VodRepository repository;

    @Transactional(readOnly = true)
    public List<VodResponse> getVodList(VodSearchCondition condition) {
        return repository.getVodList(condition);
    }
}
