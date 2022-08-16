package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {MemberConverter.class})
public interface CommunityCommentConverter {

    @Mappings({
            @Mapping(target = "memberId", source = "member.id"),
            @Mapping(target = "status", constant = "NORMAL"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "likeCount", ignore = true),
            @Mapping(target = "commentCount", ignore = true),
            @Mapping(target = "reportCount", ignore = true)
    })
    CommunityComment convert(WriteCommunityCommentRequest request);

    @Mappings({
        @Mapping(target = "relationInfo", ignore = true)
    })
    CommunityCommentResponse convert(CommunityComment entity);
}