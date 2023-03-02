package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VodConverter {

    Vod toVod(Broadcast broadcast);
}
