package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.dto.*;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberConverter.class})
public abstract class BroadcastConverter {
    public abstract List<BroadcastListResponse> toListResponse(List<BroadcastSearchResult> results);

    public abstract BroadcastCategoryResponse toResponse(BroadcastCategory category);

    @Mappings({
            @Mapping(target = "viewerCount", source = "statistics.viewerCount"),
            @Mapping(target = "heartCount", source = "statistics.heartCount")
    })
    public abstract BroadcastResponse toResponse(Broadcast broadcast);

    @Mappings({
            @Mapping(target = "id", source = "broadcast.id"),
            @Mapping(target = "status", source = "broadcast.status"),
            @Mapping(target = "viewerCount", source = "broadcast.statistics.viewerCount"),
            @Mapping(target = "heartCount", source = "broadcast.statistics.heartCount"),
            @Mapping(target = "pinMessage", ignore = true)
    })
    public abstract BroadcastResponse toResponse(Broadcast broadcast, Member createdBy);

    public abstract BroadcastResponse toResponse(BroadcastSearchResult result);

    public abstract AdminBroadcastResponse toAdminResponse(BroadcastSearchResult result);

    @Mapping(target = "participant", ignore = true)
    public abstract List<AdminBroadcastResponse> toAdminResponse(List<BroadcastSearchResult> results);

    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "thumbnailUrl", source = "request.thumbnail.url")
    public abstract Broadcast toEntity(BroadcastCreateRequest request,
                       BroadcastCategory category,
                       long memberId);

    public BroadcastStatisticsResponse converts(Broadcast broadcast) {
        ZonedDateTime endedAt = broadcast.getEndedAt() != null ? broadcast.getEndedAt() : ZonedDateTime.now();
        long duration = Duration.between(broadcast.getStartedAt(), endedAt).getSeconds();
        return BroadcastStatisticsResponse.builder()
                .totalViewerCount(broadcast.getStatistics().getTotalViewerCount())
                .maxViewerCount(broadcast.getStatistics().getMaxViewerCount())
                .viewerCount(broadcast.getStatistics().getViewerCount())
                .memberViewerCount(broadcast.getStatistics().getMemberViewerCount())
                .guestViewerCount(broadcast.getStatistics().getGuestViewerCount())
                .reportCount(broadcast.getStatistics().getReportCount())
                .heartCount(broadcast.getStatistics().getHeartCount())
                .duration(duration)
                .build();
    }

}
