package com.jocoos.mybeautip.domain.point.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.jocoos.mybeautip.member.point.MemberPoint.*;

@Getter
@RequiredArgsConstructor
public enum DefaultPointReason {

    EARNED("획득 포인트", STATE_EARNED_POINT),
    USE("사용 포인트", STATE_USE_POINT),
    GIFT("선물 포인트", STATE_PRESENT_POINT),
    REFUND("환불 포인트", STATE_REFUNDED_POINT),
    EXPIRE("만료 포인트", STATE_EXPIRED_POINT);

    private final String description;
    private final int state;

    public static String getDescriptionByState(int state) {
        return Arrays.stream(values())
                .filter(value -> value.state == state)
                .findFirst()
                .map(value -> value.description)
                .orElse("");
    }
}
