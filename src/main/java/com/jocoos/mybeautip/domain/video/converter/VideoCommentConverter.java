package com.jocoos.mybeautip.domain.video.converter;

import com.jocoos.mybeautip.domain.video.dto.WriteVideoCommentRequest;
import com.jocoos.mybeautip.member.comment.Comment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VideoCommentConverter {


    @Mapping(target = "state", constant = "DEFAULT")
    Comment convert(Long videoId, String comment, Long parentId);

    @AfterMapping
    default void convert(@MappingTarget Comment comment, WriteVideoCommentRequest request) {
        comment.setFile(request.getFilename());
    }
}
