package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BroadcastViewerConverter {
    ViewerResponse converts(ViewerSearchResult result);

    List<ViewerResponse> converts(List<ViewerSearchResult> results);
}
