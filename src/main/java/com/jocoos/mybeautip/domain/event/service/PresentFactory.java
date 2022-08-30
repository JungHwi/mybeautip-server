package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.service.impl.PresentGiftCardService;
import com.jocoos.mybeautip.domain.event.service.impl.PresentPointService;
import com.jocoos.mybeautip.domain.event.service.impl.PresentProductService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PresentFactory {

    private final PresentPointService presentPointService;
    private final PresentProductService presentProductService;
    private final PresentGiftCardService presentGiftCardService;

    public PresentService getPresentService(EventProductType type) {
        switch (type) {
            case POINT:
                return presentPointService;
            case PRODUCT:
                return presentProductService;
            case GIFT_CARD:
                return presentGiftCardService;
            default:
                throw new BadRequestException("Can't present product type - " + type.name());
        }
    }
}
