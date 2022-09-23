package com.jocoos.mybeautip.domain.video.converter;

import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.VIDEO_CATEGORY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring")
public interface VideoCategoryConverter {

    @Mappings({
        @Mapping(target = "shapeUrl", source = "shapeFile", qualifiedByName = "convertToUrl")
    })
    VideoCategoryResponse convert(VideoCategory entity);

    List<VideoCategoryResponse> convert(List<VideoCategory> entity);

    @Named(value = "convertToUrl")
    default String convertToUrl(String shapeFile) {
        return toUrl(shapeFile, VIDEO_CATEGORY);
    }

}
