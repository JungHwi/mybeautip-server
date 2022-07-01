package com.jocoos.mybeautip.domain.point.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jocoos.mybeautip.member.point.MemberPoint.*;

@Getter
@AllArgsConstructor
public enum PointStatus implements CodeValue {

    EARN("적립", new HashSet<>(Arrays.asList(STATE_EARNED_POINT, STATE_REFUNDED_POINT, STATE_PRESENT_POINT))),
    USE("사용", new HashSet<>(Arrays.asList(STATE_USE_POINT, STATE_EXPIRED_POINT)));

    private final String description;
    private final Set<Integer> legacyCodeGroup;

    public PointStatus getPointStatus(int legacyCode) {
        for (PointStatus pointStatus : PointStatus.values()) {
            if (pointStatus.getLegacyCodeGroup().contains(legacyCode)) {
                return pointStatus;
            }
        }
        return null;
    }
}
