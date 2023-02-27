package com.jocoos.mybeautip.domain.system.service.dao;

import com.jocoos.mybeautip.domain.system.code.SystemOptionType;
import com.jocoos.mybeautip.domain.system.dto.SystemOptionRequest;
import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption;
import com.jocoos.mybeautip.domain.system.persistence.repository.SystemOptionRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class SystemOptionDao {

    private final SystemOptionRepository repository;

    @Transactional(readOnly = true)
    public boolean getSystemOption(SystemOptionType type) {
        return repository.findById(type)
                .orElseThrow(() -> new BadRequestException("SystemOption not found. - " + type.name()))
                .isValue();
    }

    @Transactional
    public SystemOption update(SystemOptionRequest request) {
        SystemOption systemOption = repository.findById(request.id())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, "Not found System Option : " + request.id()));

        systemOption.updateValue(request.value());

        return systemOption;
    }
}
