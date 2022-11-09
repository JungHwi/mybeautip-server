package com.jocoos.mybeautip.domain.event.dto;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventProductRequest {

    private Long id;

    private Long eventId;

    private EventProductType type;

    private int price;

}
