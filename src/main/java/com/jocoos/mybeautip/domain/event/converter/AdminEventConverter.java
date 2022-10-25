package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.dto.AdminEventResponse;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class AdminEventConverter {

    public List<AdminEventResponse> convertAllImages(List<EventSearchResult> results) {
        return results.stream()
                .map(result -> convert(result.getEvent(), result.getJoinCount()))
                .toList();
    }

    public List<EventStatusResponse> convert(Map<EventStatus, Long> joinCountMap) {
        return EventStatusResponse.from(joinCountMap);
    }

    @Mapping(target = "detailImageUrl", source = "event.imageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "bannerImageUrl", source = "event.thumbnailImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "rollingBannerImageUrl", source = "event.bannerImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "shareSnsImageUrl", source = "event.shareSquareImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "shareWebImageUrl", source = "event.shareRectangleImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "point", source = "event.needPoint")
    public abstract AdminEventResponse convertWithAllImages(Event event, Long joinCount);

    @Mapping(target = "detailImageUrl", ignore = true)
    @Mapping(target = "bannerImageUrl", source = "event.thumbnailImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "rollingBannerImageUrl", ignore = true)
    @Mapping(target = "shareSnsImageUrl", ignore = true)
    @Mapping(target = "shareWebImageUrl", ignore = true)
    @Mapping(target = "point", source = "event.needPoint")
    abstract AdminEventResponse convert(Event event, Long joinCount);

    @Named("fileToUrl")
    protected String fileToUrl(String file) {
        return ImageUrlConvertUtil.toUrl(file, UrlDirectory.EVENT);
    }
}
