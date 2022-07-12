package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventProductResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventConverter {

    EventResponse convertToResponse(Event event);

    EventListResponse convertToListResponse(Event event);

    List<EventListResponse> convertToListResponse(List<Event> eventList);

    EventProductResponse convertToProduct(EventProduct product);

    List<EventProductResponse> convertToProduct(List<EventProduct> productList);
}