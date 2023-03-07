package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastListResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
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

    @Mappings({
            @Mapping(target = "id", source = "broadcast.id"),
            @Mapping(target = "status", source = "broadcast.status")
    })
    BroadcastResponse toResponse(Broadcast broadcast, Member member);

    BroadcastResponse toResponse(BroadcastSearchResult result, String streamKey);

    AdminBroadcastResponse toAdminResponse(BroadcastSearchResult result);

    List<AdminBroadcastResponse> toAdminResponse(List<BroadcastSearchResult> results);

    @Mapping(target = "title", source = "request.title")
    Broadcast toEntity(BroadcastCreateRequest request,
                       BroadcastCategory category,
                       long memberId);
}
