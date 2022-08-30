package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventProductResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventConverter {

    @Mappings({
            @Mapping(target = "imageUrl", source = "imageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "bannerImageUrl", source = "bannerImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "shareSquareImageUrl", source = "shareSquareImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "shareRectangleImageUrl", source = "shareRectangleImageFile", qualifiedByName = "fileToUrl")
    })
    EventResponse convertToResponse(Event event);

    @Mappings({
            @Mapping(target = "thumbnailImageUrl", source = "thumbnailImageFile", qualifiedByName = "fileToUrl"),
            @Mapping(target = "bannerImageUrl", source = "bannerImageFile", qualifiedByName = "fileToUrl"),
    })
    EventListResponse convertToListResponse(Event event);

    List<EventListResponse> convertToListResponse(List<Event> eventList);

    @Mappings({
            @Mapping(target = "imageUrl", source = "imageFile", qualifiedByName = "fileToUrlForProduct"),
    })
    EventProductResponse convertToProduct(EventProduct product);

    List<EventProductResponse> convertToProduct(List<EventProduct> productList);

    @Named("fileToUrl")
    default String fileToUrl(String file) {
        return ImageUrlConvertUtil.toUrl(file, UrlDirectory.EVENT);
    }

    @Named("fileToUrlForProduct")
    default String fileToUrlForProduct(String file) {
        return ImageUrlConvertUtil.toUrl(file, UrlDirectory.EVENT_PRODUCT);
    }
}