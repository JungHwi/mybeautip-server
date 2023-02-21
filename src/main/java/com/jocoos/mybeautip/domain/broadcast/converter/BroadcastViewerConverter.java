package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BroadcastViewerConverter {

    @Mappings({
            @Mapping(target = "isSuspended", source = "suspended"),
            @Mapping(target = "username", ignore = true),
            @Mapping(target = "avatarUrl", ignore = true),
    })
    ViewerResponse converts(BroadcastViewer entity);

    @Mappings({
            @Mapping(target = "isSuspended", source = "suspended")
    })
    ViewerResponse converts(ViewerSearchResult result);

    List<ViewerResponse> converts(List<ViewerSearchResult> results);
}
