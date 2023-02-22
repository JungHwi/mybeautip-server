package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BroadcastConverter {
    List<BroadcastResponse> toResponse(List<BroadcastSearchResult> results);

    List<AdminBroadcastResponse> toAdminResponse(List<BroadcastSearchResult> results);
}
