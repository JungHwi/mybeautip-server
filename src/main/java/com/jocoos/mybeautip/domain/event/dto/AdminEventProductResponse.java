package com.jocoos.mybeautip.domain.event.dto;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminEventProductResponse {

    private EventProductType type;

    private int price;
}
