package com.jocoos.mybeautip.domain.vod.converter;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VodConverter {

    @Mapping(target = "isVisible", constant = "false")
    @Mapping(target = "status", constant = "CREATED")
    Vod init(Broadcast broadcast);
}
