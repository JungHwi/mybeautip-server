package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse.CommunityCommentResponseBuilder;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityCommentResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberConverter.class})
public interface CommunityCommentConverter {

    @Mappings({
            @Mapping(target = "memberId", source = "member.id"),
            @Mapping(target = "status", constant = "NORMAL"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "file", ignore = true),
            @Mapping(target = "likeCount", ignore = true),
            @Mapping(target = "commentCount", ignore = true),
            @Mapping(target = "reportCount", ignore = true)
    })
    CommunityComment convert(WriteCommunityCommentRequest request);

    @AfterMapping
    default void convert(@MappingTarget CommunityComment comment, WriteCommunityCommentRequest request) {
        comment.setFile(request.getFilename());
    }

    @Mappings({
        @Mapping(target = "relationInfo", ignore = true),
        @Mapping(target = "fileUrl", ignore = true)
    })
    CommunityCommentResponse convert(CommunityComment entity);

    @AfterMapping
    default void convert(@MappingTarget CommunityCommentResponseBuilder response, CommunityComment communityComment) {
        response.fileUrl(communityComment.getFileUrl());
    }

    List<CommunityCommentResponse> convert(List<CommunityComment> entity);

    MyCommunityCommentResponse convertToMyComment(CommunityComment entity);

    List<MyCommunityCommentResponse> convertToMyComment(List<CommunityComment> entities);
}
