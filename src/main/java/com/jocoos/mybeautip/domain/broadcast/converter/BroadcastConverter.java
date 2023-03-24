package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.dto.*;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastStatistics;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberConverter.class})
public interface BroadcastConverter {
    List<BroadcastListResponse> toListResponse(List<BroadcastSearchResult> results);

    BroadcastCategoryResponse toResponse(BroadcastCategory category);
    BroadcastResponse toResponse(Broadcast broadcast);

    @Mappings({
            @Mapping(target = "id", source = "broadcast.id"),
            @Mapping(target = "status", source = "broadcast.status"),
            @Mapping(target = "viewerCount", source = "broadcast.statistics.viewerCount")
    })
    BroadcastResponse toResponse(Broadcast broadcast, Member createdBy);

    BroadcastResponse toResponse(BroadcastSearchResult result);

    AdminBroadcastResponse toAdminResponse(BroadcastSearchResult result);

    @Mapping(target = "participant", ignore = true)
    List<AdminBroadcastResponse> toAdminResponse(List<BroadcastSearchResult> results);

    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "thumbnailUrl", source = "request.thumbnail.url")
    Broadcast toEntity(BroadcastCreateRequest request,
                       BroadcastCategory category,
                       long memberId);


    BroadcastStatisticsResponse converts(BroadcastStatistics entity);
}
