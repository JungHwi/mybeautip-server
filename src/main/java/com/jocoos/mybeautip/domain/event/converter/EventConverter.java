package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventProductResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.global.util.ImageFileConvertUtil;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventConverter {

    EventResponse convertToResponse(Event event);

    @Mappings({
            @Mapping(target = "imageUrl", ignore = true)
    })
    EventListResponse convertToListResponse(Event event);

    @AfterMapping
    default void convertToListResponse(@MappingTarget EventListResponse response, Event event) {
        response.setImageUrl(ImageFileConvertUtil.convertToThumbnail(event.getImageUrl()));
    }

    List<EventListResponse> convertToListResponse(List<Event> eventList);

    EventProductResponse convertToProduct(EventProduct product);

    List<EventProductResponse> convertToProduct(List<EventProduct> productList);
}