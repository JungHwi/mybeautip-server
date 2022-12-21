package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.AdminMemberResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.jocoos.mybeautip.domain.community.dto.AdminCommunityCommentResponse.AdminCommunityCommentResponseBuilder;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;

@Mapper(componentModel = "spring")
public interface AdminCommunityCommentConverter {

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "member", source = "entity.member", qualifiedByName = "toAdminMemberResponse")
    AdminCommunityCommentResponse convert(CommunityComment entity, List<AdminCommunityCommentResponse> children);

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "member", source = "comment.member", qualifiedByName = "toAdminMemberResponse")
    AdminCommunityCommentResponse convert(CommunityComment comment);

    @Named(value = "toAdminMemberResponse")
    default AdminMemberResponse convertToUrl(Member member) {
        return AdminMemberResponse.from(member);
    }

    @AfterMapping
    default void convert(@MappingTarget AdminCommunityCommentResponseBuilder response, CommunityComment comment) {
        response.fileUrl(comment.getFileUrl());
    }

    default List<AdminCommunityCommentResponse> convert(List<CommunityComment> entities, List<CommunityComment> children, CommunityCategory category) {
        Map<Long, List<AdminCommunityCommentResponse>> childrenMap = children.stream()
                .map(this::convert)
                .collect(Collectors.groupingBy(AdminCommunityCommentResponse::getParentId));

        List<AdminCommunityCommentResponse> responses = entities.stream()
                .map(entity -> convert(entity, childrenMap.getOrDefault(entity.getId(), new ArrayList<>())))
                .toList();

        if (category.isCategoryType(BLIND)) {
            responses.forEach(AdminCommunityCommentResponse::blindMember);
        }

        return responses;
    }
}
