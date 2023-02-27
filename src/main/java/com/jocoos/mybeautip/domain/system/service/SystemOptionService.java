package com.jocoos.mybeautip.domain.system.service;

import com.jocoos.mybeautip.domain.system.code.SystemOptionType;
import com.jocoos.mybeautip.domain.system.converter.SystemOptionConverter;
import com.jocoos.mybeautip.domain.system.dto.SystemOptionRequest;
import com.jocoos.mybeautip.domain.system.dto.SystemOptionResponse;
import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption;
import com.jocoos.mybeautip.domain.system.service.dao.SystemOptionDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemOptionService {

    private final SystemOptionDao dao;
    private final SystemOptionConverter converter;

    @Transactional(readOnly = true)
    public SystemOptionResponse get(SystemOptionType id) {
        SystemOption systemOption = dao.get(id);
        return converter.converts(systemOption);
    }

    @Transactional
    public SystemOptionResponse update(SystemOptionRequest request) {
        SystemOption systemOption = dao.update(request);
        return converter.converts(systemOption);
    }
}
