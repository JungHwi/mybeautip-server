package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.dto.AdminEventResponse;
import com.jocoos.mybeautip.domain.event.dto.EditEventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static java.lang.Boolean.TRUE;

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
            @Mapping(target = "id", source = "event.id"),
            @Mapping(target = "type", source = "event.type"),
            @Mapping(target = "relationId", source = "event.relationId"),
            @Mapping(target = "status", source = "request.status"),
            @Mapping(target = "isVisible", source = "request.isVisible"),
            @Mapping(target = "title", source = "request.title"),
            @Mapping(target = "description", source = "request.description"),
            @Mapping(target = "needPoint", source = "request.needPoint"),
            @Mapping(target = "startAt", source = "request.startAt"),
            @Mapping(target = "endAt", source = "request.endAt"),
            @Mapping(target = "reservationAt", source = "request.reservationAt"),
            @Mapping(target = "createdAt", source = "event.createdAt"),
            @Mapping(target = "thumbnailImageFile", ignore = true),
            @Mapping(target = "imageFile", ignore = true),
            @Mapping(target = "shareSquareImageFile", ignore = true),
            @Mapping(target = "shareRectangleImageFile", ignore = true),
            @Mapping(target = "bannerImageFile", ignore = true),
            @Mapping(target = "eventProductList", ignore = true),
            @Mapping(target = "eventJoinList", ignore = true),
    })
    public abstract Event convertForEdit(Event event, EditEventRequest request);

    @AfterMapping
    public void convert(@MappingTarget Event event, Event originalEvent, EditEventRequest request) {
        event.setThumbnailImageFile(getFileName(request.getThumbnailImageUrl()));
        event.setImageFile(getFileName(request.getDetailImageUrl()));
        event.setShareRectangleImageFile(getFileName(request.getShareRectangleImageUrl()));
        event.setShareSquareImageFile(getFileName(request.getShareSquareImageUrl()));
        event.setBannerImageFile(getFileName(request.getBannerImageUrl()));

        List<EventProduct> eventProductList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(originalEvent.getEventProductList())) {
            EventProduct product = originalEvent.getEventProductList().get(0);
            product.setType(request.getProduct().getType());
            product.setPrice(request.getProduct().getPrice());
            eventProductList.add(product);
        }
        event.setEventProductList(eventProductList);
    }

    @Mappings({
            @Mapping(target = "eventProductList", source = "product"),
            @Mapping(target = "eventJoinList", ignore = true),
    })
    public abstract Event convert(EventRequest request);

    @AfterMapping
    public void convert(@MappingTarget Event event, EventRequest request) {
        event.setThumbnailImageFile(getFileName(request.getThumbnailImageUrl()));
        event.setImageFile(getFileName(request.getDetailImageUrl()));
        event.setShareRectangleImageFile(getFileName(request.getShareRectangleImageUrl()));
        event.setShareSquareImageFile(getFileName(request.getShareSquareImageUrl()));
        event.setBannerImageFile(getFileName(request.getBannerImageUrl()));
    }

    @Mappings({
            @Mapping(target = "detailImageUrl", source = "event.imageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "thumbnailImageUrl", source = "event.thumbnailImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "bannerImageUrl", source = "event.bannerImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "shareSquareImageUrl", source = "event.shareSquareImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "shareRectangleImageUrl", source = "event.shareRectangleImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "product", source = "event.eventProductList"),
            @Mapping(target = "needPoint", source = "event.needPoint")
    })
    public abstract AdminEventResponse convertWithAllImages(Event event, Long joinCount);

    @Mappings({
            @Mapping(target = "detailImageUrl", ignore = true),
            @Mapping(target = "thumbnailImageUrl", source = "event.thumbnailImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "bannerImageUrl", ignore = true),
            @Mapping(target = "shareSquareImageUrl", ignore = true),
            @Mapping(target = "shareRectangleImageUrl", ignore = true),
            @Mapping(target = "needPoint", source = "event.needPoint")
    })
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

    @Named("isTrueOrNull")
    protected Boolean isTrueOrNull(Boolean isTopFix) {
        return TRUE.equals(isTopFix) ? true : null;
    }
}
