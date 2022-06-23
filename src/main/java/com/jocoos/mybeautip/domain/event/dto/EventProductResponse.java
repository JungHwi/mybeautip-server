package com.jocoos.mybeautip.domain.event.dto;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventProductResponse {

    private long id;

    private EventProductType type;

    private String name;

    private String imageUrl;
}
