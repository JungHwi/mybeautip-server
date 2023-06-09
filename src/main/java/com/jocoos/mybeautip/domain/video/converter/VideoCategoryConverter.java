package com.jocoos.mybeautip.domain.video.converter;

import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import com.jocoos.mybeautip.video.VideoCategoryMapping;
import org.mapstruct.*;

import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.VIDEO_CATEGORY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring")
public interface VideoCategoryConverter {

    @Mappings({
        @Mapping(target = "shapeUrl", source = "shapeFile", qualifiedByName = "convertToUrl")
    })
    @Named("all")
    VideoCategoryResponse convert(VideoCategory entity);

    @IterableMapping(qualifiedByName = "all")
    List<VideoCategoryResponse> convert(List<VideoCategory> entity);

    @Mappings({
            @Mapping(target = "id", source = "videoCategory.id"),
            @Mapping(target = "type", source = "videoCategory.type"),
            @Mapping(target = "title", source = "videoCategory.title"),
            @Mapping(target = "shapeUrl", source = "videoCategory.shapeFile", qualifiedByName = "convertToUrl"),
            @Mapping(target = "maskType", source = "videoCategory.maskType")
    })
    VideoCategoryResponse convertMapping(VideoCategoryMapping mapping);

    List<VideoCategoryResponse> convertMapping(List<VideoCategoryMapping> entity);

    @Named(value = "convertToUrl")
    default String convertToUrl(String shapeFile) {
        return toUrl(shapeFile, VIDEO_CATEGORY);
    }


    @IterableMapping(qualifiedByName = "exclude_shape_info")
    List<VideoCategoryResponse> excludeShapeInfo(List<VideoCategory> categories);

    @Named("exclude_shape_info")
    @Mappings({
            @Mapping(target = "shapeUrl", ignore = true),
            @Mapping(target = "maskType", ignore = true)
    })
    VideoCategoryResponse excludeShapeInfo(VideoCategory category);
}
