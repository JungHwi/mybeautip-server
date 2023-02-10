package com.jocoos.mybeautip.domain.system.converter;

import com.jocoos.mybeautip.domain.system.dto.SystemOptionResponse;
import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SystemOptionConverter {

    SystemOptionResponse converts(SystemOption entity);
}
