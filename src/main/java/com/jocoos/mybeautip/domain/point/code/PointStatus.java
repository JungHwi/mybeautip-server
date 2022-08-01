package com.jocoos.mybeautip.domain.point.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.member.point.MemberPoint.*;

@Getter
@AllArgsConstructor
public enum PointStatus implements CodeValue {

    WILL_BE_EARNED("획득 예정", STATE_WILL_BE_EARNED),
    EARNED("획득", STATE_EARNED_POINT),
    USED("사용", STATE_USE_POINT),
    EXPIRED("만료", STATE_EXPIRED_POINT),
    REFUNDED("환불", STATE_REFUNDED_POINT),
    PRESENT("선물", STATE_PRESENT_POINT);

    private final String description;
    private final int legacyCode;

    private final static Map<Integer, PointStatus> pointStatusMap = Arrays.stream(PointStatus.values()).collect(Collectors.toMap(PointStatus::getLegacyCode, e -> e));

    public static PointStatus getPointStatus(int legacyCode) {
        return pointStatusMap.get(legacyCode);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
