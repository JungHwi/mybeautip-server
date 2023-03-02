package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastListResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberConverter.class})
public interface BroadcastConverter {
    List<BroadcastListResponse> toListResponse(List<BroadcastSearchResult> results);
    BroadcastResponse toResponse(BroadcastSearchResult result);

    AdminBroadcastResponse toAdminResponse(BroadcastSearchResult result);

    List<AdminBroadcastResponse> toAdminResponse(List<BroadcastSearchResult> results);

    @Mapping(target = "title", source = "request.title")
    Broadcast toEntity(BroadcastCreateRequest request,
                       ExternalBroadcastInfo externalInfo,
                       BroadcastCategory category,
                       long memberId);
}
