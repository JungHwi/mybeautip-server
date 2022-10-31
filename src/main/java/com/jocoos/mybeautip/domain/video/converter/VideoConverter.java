package com.jocoos.mybeautip.domain.video.converter;

import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.video.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberConverter.class, VideoCategoryConverter.class})
public interface VideoConverter {

    @Mappings({
            @Mapping(target = "category", source = "categoryMapping"),
            @Mapping(target = "owner", source = "member")
    })
    VideoResponse converts(Video video);

    List<VideoResponse> converts(List<Video> videos);
}
