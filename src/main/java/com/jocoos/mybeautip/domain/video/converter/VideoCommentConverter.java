package com.jocoos.mybeautip.domain.video.converter;

import com.jocoos.mybeautip.domain.video.dto.WriteVideoCommentRequest;
import com.jocoos.mybeautip.member.comment.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VideoCommentConverter {


    @Mappings({
            @Mapping(target = "state", constant = "DEFAULT"),
            @Mapping(target = "comment", source = "request.contents"),
            @Mapping(target = "parentId", source = "request.parentId"),
            @Mapping(target = "file", ignore = true),
    })
    Comment convert(Long videoId, WriteVideoCommentRequest request);

    @AfterMapping
    default void convert(@MappingTarget Comment comment, WriteVideoCommentRequest request) {
        comment.setFile(request.getFilename());
    }
}
