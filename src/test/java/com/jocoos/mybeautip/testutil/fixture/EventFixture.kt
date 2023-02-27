package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.event.code.EventProductType
import com.jocoos.mybeautip.domain.event.code.EventStatus
import com.jocoos.mybeautip.domain.event.code.EventStatus.PROGRESS
import com.jocoos.mybeautip.domain.event.code.EventType
import com.jocoos.mybeautip.domain.event.code.EventType.DRIP
import com.jocoos.mybeautip.domain.event.persistence.domain.Event
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct
import com.jocoos.mybeautip.domain.event.persistence.domain.FixSorting
import java.time.ZonedDateTime


fun makeEvent(
    id: Long = 1,
    type: EventType = DRIP,
    relationId: Long = 3,
    status: EventStatus = PROGRESS,
    isVisible: Boolean = true,
    statusSorting: Int = 0,
    title: String = "title",
    description: String = "description",
    imageFile: String = "imageFile",
    thumbnailImageFile: String = "thumbnailFile",
    bannerImageFile: String = "bannerImageFile",
    shareSquareImageFile: String = "shareSqImage",
    shareRectangleImageFile: String = "shareRectImage",
    needPoint: Int = 0,
    startAt: ZonedDateTime = ZonedDateTime.now().minusDays(1),
    endAt: ZonedDateTime = ZonedDateTime.now().plusDays(1),
    reservationAt: ZonedDateTime = ZonedDateTime.now(),
    eventProductList: List<EventProduct> = emptyList(),
    eventJoinList: List<EventJoin> = emptyList()
): Event {
    return Event(
        id,
        type,
        relationId,
        status,
        isVisible,
        statusSorting,
        title,
        description,
        imageFile,
        thumbnailImageFile,
        bannerImageFile,
        shareSquareImageFile,
        shareRectangleImageFile,
        needPoint,
        startAt,
        endAt,
        reservationAt,
        FixSorting.unFix(),
        eventProductList,
        eventJoinList
    )
}

fun makeEvents(eventNumber: Int, status: EventStatus = PROGRESS) : List<Event> {
    return IntRange(0, eventNumber)
        .map { makeEvent(status = status) }
        .toList()
}

fun makeEventProduct(): EventProduct {
    return EventProduct.builder()
        .type(EventProductType.POINT)
        .price(3000)
        .build()
}
