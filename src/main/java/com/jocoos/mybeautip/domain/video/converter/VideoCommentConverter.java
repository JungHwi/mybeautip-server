package com.jocoos.mybeautip.domain.video.converter;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoCommentConverter {


    @Mapping(target = "state", constant = "DEFAULT")
    Comment convert(Long videoId, String comment, Long parentId);
}
