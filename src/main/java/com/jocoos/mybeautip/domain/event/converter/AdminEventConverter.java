package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.dto.AdminEventResponse;
import com.jocoos.mybeautip.domain.event.dto.EventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.util.FileUtil.getFilename;

@Mapper(componentModel = "spring", uses = {AdminEventProductConverter.class})
public abstract class AdminEventConverter {

    public List<AdminEventResponse> convert(List<EventSearchResult> results) {
        return results.stream()
                .map(result -> convert(result.getEvent(), result.getJoinCount()))
                .toList();
    }

    public List<EventStatusResponse> convert(Map<EventStatus, Long> joinCountMap) {
        return EventStatusResponse.from(joinCountMap);
    }

    @Mappings({
            @Mapping(target = "eventProductList", source = "product"),
            @Mapping(target = "eventJoinList", ignore = true),
    })
    public abstract Event convert(EventRequest request);

    @AfterMapping
    public void convert(@MappingTarget Event event, EventRequest request) {
        event.setThumbnailImageFile(getFilename(request.getThumbnailImageUrl()));
        event.setImageFile(getFilename(request.getDetailImageUrl()));
        event.setShareRectangleImageFile(getFilename(request.getShareRectangleImageUrl()));
        event.setShareSquareImageFile(getFilename(request.getShareSquareImageUrl()));
        event.setBannerImageFile(getFilename(request.getBannerImageUrl()));
    }

    @Mapping(target = "detailImageUrl", source = "event.imageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "thumbnailImageUrl", source = "event.thumbnailImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "bannerImageUrl", source = "event.bannerImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "shareSquareImageUrl", source = "event.shareSquareImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "shareRectangleImageUrl", source = "event.shareRectangleImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "product", source = "event.eventProductList")
    @Mapping(target = "needPoint", source = "event.needPoint")
    public abstract AdminEventResponse convertWithAllImages(Event event, Long joinCount);

    @Mapping(target = "detailImageUrl", ignore = true)
    @Mapping(target = "thumbnailImageUrl", source = "event.thumbnailImageFile", qualifiedByName = "fileToUrl")
    @Mapping(target = "bannerImageUrl", ignore = true)
    @Mapping(target = "shareSquareImageUrl", ignore = true)
    @Mapping(target = "shareRectangleImageUrl", ignore = true)
    @Mapping(target = "needPoint", source = "event.needPoint")
    abstract AdminEventResponse convert(Event event, Long joinCount);

    @Mappings({
            @Mapping(target = "detailImageUrl", source = "event.imageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "thumbnailImageUrl", source = "event.thumbnailImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "bannerImageUrl", source = "event.bannerImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "shareSquareImageUrl", source = "event.shareSquareImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "shareRectangleImageUrl", source = "event.shareRectangleImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "joinCount", constant = "0L"),
            @Mapping(target = "product", source = "eventProductList")
    })
    public abstract AdminEventResponse convert(Event event);

    @Named("fileToUrl")
    protected String fileToUrl(String file) {
        return ImageUrlConvertUtil.toUrl(file, UrlDirectory.EVENT);
    }
}
