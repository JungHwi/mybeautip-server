package com.jocoos.mybeautip.domain.system.service;

import com.jocoos.mybeautip.domain.system.converter.SystemOptionConverter;
import com.jocoos.mybeautip.domain.system.dto.SystemOptionRequest;
import com.jocoos.mybeautip.domain.system.dto.SystemOptionResponse;
import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption;
import com.jocoos.mybeautip.domain.system.service.dao.SystemOptionDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemOptionService {

    private final SystemOptionDao dao;
    private final SystemOptionConverter converter;

    public SystemOptionResponse update(SystemOptionRequest request) {
        SystemOption systemOption = dao.update(request);
        return converter.converts(systemOption);
    }
}
