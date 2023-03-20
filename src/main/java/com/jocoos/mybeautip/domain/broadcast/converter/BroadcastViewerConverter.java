package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import com.jocoos.mybeautip.member.Member;
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
            @Mapping(target = "isSuspended", source = "entity.suspended")
    })
    ViewerResponse converts(BroadcastViewer entity, String username, String avatarUrl);

    @Mappings({
            @Mapping(target = "isSuspended", source = "suspended")
    })
    ViewerResponse converts(ViewerSearchResult result);

    List<ViewerResponse> converts(List<ViewerSearchResult> results);

    @Mappings({
            @Mapping(target = "status", constant = "ACTIVE"),
            @Mapping(target = "type", source = "type"),
            @Mapping(target = "memberId", source = "member.id"),
            @Mapping(target = "isSuspended", ignore = true),
            @Mapping(target = "suspendedAt", ignore = true),
            @Mapping(target = "joinedAt", ignore = true)
    })
    ViewerResponse converts(Member member, BroadcastViewerType type);

    @Mappings({
            @Mapping(target = "status", constant = "ACTIVE"),
            @Mapping(target = "type", constant = "GUEST"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "memberId", ignore = true),
            @Mapping(target = "avatarUrl", ignore = true),
            @Mapping(target = "isSuspended", ignore = true),
            @Mapping(target = "suspendedAt", ignore = true),
            @Mapping(target = "joinedAt", ignore = true)
    })
    ViewerResponse toGuest(String username);
}
