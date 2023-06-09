package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.service.EventTypeService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public abstract class EventTypeAbstractService implements EventTypeService {

    public EventProduct winPrize(List<EventProduct> productList) {
        if (CollectionUtils.isEmpty(productList)) {
            return null;
        }

        if (productList.size() == 1) {
            return productList.get(0);
        }

        int winningIndex = RandomUtils.getRandomIndex(productList.size());
        EventProduct winningProduct = productList.get(winningIndex);
        return winningProduct.winPrize();
    }

    public String getRecipientInfo(EventProduct product, Address address) {
        switch (product.getType()) {
            case GIFT_CARD:
                return address.getPhone();
            case PRODUCT:
                return address.getWholeAddress();
            default:
                return "";
        }
    }

    public String getRecipientInfo(Address address) {
        return address.getDeliveryInfo();
    }

    public void validEvent(Event event, Address address) {
        this.validEvent(event);

        List<EventProduct> eventProductList = event.getEventProductList();
        Set<EventProductType> eventProductTypes = eventProductList.stream()
                .filter(product -> product.getType() == EventProductType.PRODUCT || product.getType() == EventProductType.GIFT_CARD)
                .map(EventProduct::getType)
                .collect(Collectors.toSet());

        if (eventProductTypes.contains(EventProductType.PRODUCT)) {
            validAddress(address);
            validPhone(address);
        }

        if (eventProductTypes.contains(EventProductType.GIFT_CARD)) {
            validPhone(address);
        }
    }

    public void validEvent(Event event) {
        if (event == null) {
            throw new BadRequestException("Event is not available");
        }
        validEventStatus(event.getStatus());
        validEventVisible(event.getIsVisible());
        validEventPeriod(event.getStartAt(), event.getEndAt());
    }

    public void validAddress(Address address) {
        if (address == null || StringUtils.isBlank(address.getZipNo())) {
            throw new BadRequestException(ErrorCode.NO_ADDRESS, "no_address");
        }
    }

    public void validPhone(Address address) {
        if (address == null || StringUtils.isBlank(address.getPhone())) {
            throw new BadRequestException(ErrorCode.NO_PHONE, "no_phone");
        }
    }

    private void validEventStatus(EventStatus status) {
        if (!status.isCanJoin()) {
            throw new BadRequestException(ErrorCode.CAN_NOT_JOIN_STATUS, "This event type is " + status);
        }
    }

    private void validEventVisible(boolean visible) {
        if (!visible) {
            throw new BadRequestException(ErrorCode.CAN_NOT_JOIN_STATUS, "This event visible is false.");
        }
    }

    private void validEventPeriod(ZonedDateTime startAt, ZonedDateTime endAt) {
        validEventStartDate(startAt);
        validEventEndDate(endAt);
    }

    private void validEventStartDate(ZonedDateTime startAt) {
        if (!ZonedDateTime.now().isAfter(startAt)) {
            throw new BadRequestException(ErrorCode.NOT_STARTED_EVENT, "Event start " + startAt);
        }
    }

    private void validEventEndDate(ZonedDateTime endAt) {
        if (endAt == null){
            return;
        }

        if (!ZonedDateTime.now().isBefore(endAt)) {
            throw new BadRequestException(ErrorCode.ALREADY_ENDED_EVENT, "Event ended " + endAt);
        }
    }
}
