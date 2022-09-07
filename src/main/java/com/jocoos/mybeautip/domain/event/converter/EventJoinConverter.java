package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.dto.EventJoinHistoryResponse;
import com.jocoos.mybeautip.domain.event.dto.EventJoinResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventJoinConverter {

    @Mapping(target = "result", source = "eventProductId")
    EventJoinResponse convertsToJoin(EventJoin eventJoin);

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
        switch (event.getType()) {
            case SIGNUP:
            case INVITE:
                description = "적립 완료";
                break;
            case ROULETTE:
                description = eventJoin.getEventProduct().getName();
                break;
            case JOIN:
                switch (eventJoin.getStatus()) {
                    case JOIN:
                        description = "응모 완료";
                        break;
                    case WIN:
                        description = eventJoin.getEventProduct().getName();
                        break;
                }
                break;
            case DRIP:
                switch (eventJoin.getStatus()) {
                    case JOIN:
                        description = "응모 완료";
                        break;
                    case WIN:
                        description = "이벤트 당첨";
                        break;
                }
                break;
            default:
                throw new BadRequestException("event type is not supported. type - " + event.getType());
        }
        eventJoinHistoryResponse.setDescription(description);
    }
}