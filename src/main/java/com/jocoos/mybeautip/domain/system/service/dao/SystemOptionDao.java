package com.jocoos.mybeautip.domain.system.service.dao;

import com.jocoos.mybeautip.domain.system.dto.SystemOptionRequest;
import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption;
import com.jocoos.mybeautip.domain.system.persistence.repository.SystemOptionRepository;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class SystemOptionDao {

    private final SystemOptionRepository repository;

    @Transactional
    public SystemOption update(SystemOptionRequest request) {
        SystemOption systemOption = repository.findById(request.id())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, "Not found System Option : " + request.id()));

        systemOption.updateValue(request.value());

        return systemOption;
    }
}
