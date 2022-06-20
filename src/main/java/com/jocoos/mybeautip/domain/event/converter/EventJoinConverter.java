package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.dto.EventJoinHistoryResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.exception.BadRequestException;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventJoinConverter {

    @Mappings({
            @Mapping(target = "title", source = "event.title"),
            @Mapping(target = "description", ignore = true)
    })
    EventJoinHistoryResponse convertsToResponse(EventJoin eventJoin);

    List<EventJoinHistoryResponse> convertsToResponse(List<EventJoin> eventJoin);

    @AfterMapping
    default void convertsToResponse(@MappingTarget EventJoinHistoryResponse eventJoinHistoryResponse, EventJoin eventJoin) {
        String description = "";
        Event event = eventJoin.getEvent();
        EventProduct eventProduct = eventJoin.getEventProduct();
        switch (event.getType()) {
            case SIGNUP:
            case INVITE:
                description = "적립 완료";
                break;
            case ROULETTE:
                description = eventProduct.getName();
                break;
            case JOIN:
                switch (eventJoin.getStatus()) {
                    case JOIN:
                        description = "응모 완료";
                        break;
                    case WIN:
                        description = eventProduct.getName();
                        break;
                }
                break;
            default:
                throw new BadRequestException("not_supported_event", "event type is not supported. type - " + event.getType());
        }
        eventJoinHistoryResponse.setDescription(description);
    }
}