package com.jocoos.mybeautip.domain.point.service.detail;

import com.jocoos.mybeautip.domain.point.service.MemberPointDetailService;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_EARNED_POINT;
import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_UNDER_ZERO_POINT;
import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("[MemberPointDetailService] - 마이너스 포인트 2개 있을 때 테스트")
class TwoUnderZeroPointDetailTest extends DefaultMemberPointDetailServiceTest {

    @Autowired
    private MemberPointDetailService sut;

    private int currentPoint;
    private MemberPointDetail firstUnderZeroDetail;
    private MemberPointDetail secondUnderZeroDetail;

    @PostConstruct
    void postConstruct() {
        super.init();
    }

    @BeforeEach
    void init() {
        firstUnderZeroDetail = super.setInitUnderZeroPoint();
        secondUnderZeroDetail = super.setInitUnderZeroPoint();
        currentPoint = firstUnderZeroDetail.getPoint() + secondUnderZeroDetail.getPoint();
    }


    @DisplayName("적립 : 2개 마이너스 포인트 -> 마이너스 포인트 1개 채우고 나머지 마이너스 포인트 못채움")
    @Test
    void earnTwoUnderZeroJustOneFull() {

        // given
        // 첫번째 마이너스 포인트는 채우고 두번째 마이너스 포인트는 못채울 정도로 부여 -> 35포인트
        final int earnPoint = -(firstUnderZeroDetail.getPoint() + secondUnderZeroDetail.getPoint() / 2);
        MemberPoint earnMemberPoint = super.createEarnMemberPoint(earnPoint);

        // when
        List<MemberPointDetail> details = sut.earnPoints(earnMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(2);
        details.sort(Comparator.comparingLong(MemberPointDetail::getId));

        // 첫번째 마이너스 포인트 채움 검증
        MemberPointDetail firstFillUnderZeroDetail = details.get(0);
        MemberPointDetail firstFillUnderZeroExpected = MemberPointDetail.builder()
                .point(-firstUnderZeroDetail.getPoint())
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(firstUnderZeroDetail.getMemberPointId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(firstFillUnderZeroDetail, firstFillUnderZeroExpected);

        // 두번째 마이너스 포인트 못채우고 세이브 검증
        MemberPointDetail remainingPointActual = details.get(1);
        MemberPointDetail remainingPointExpected = MemberPointDetail.builder()
                .point(earnPoint + firstUnderZeroDetail.getPoint())
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(secondUnderZeroDetail.getMemberPointId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(remainingPointActual, remainingPointExpected);

        // 채운 마이너스 포인트는 부모 세팅, 아니면 그대로 널값
        assertThat(firstUnderZeroDetail.getParentId()).isEqualTo(firstUnderZeroDetail.getMemberPointId());
        assertThat(secondUnderZeroDetail.getParentId()).isNull();
    }

    @DisplayName("적립 : 2개 - 포인트 -> - 포인트 다 채우고 나머지 + 포인트로")
    @Test
    void earnTwoUnderZeroToUpperZero() {

        // given
        // 두 마이너스 포인트 다 채우고 남을 정도로 부여
        final int earnPoint = -(currentPoint * 2);
        MemberPoint earnMemberPoint = createEarnMemberPoint(earnPoint);

        // when
        List<MemberPointDetail> details = sut.earnPoints(earnMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(3);
        details.sort(Comparator.comparingLong(MemberPointDetail::getId));

        MemberPointDetail firstFillUnderZeroDetail = details.get(0);
        MemberPointDetail firstFillUnderZeroExpected = MemberPointDetail.builder()
                .point(-firstUnderZeroDetail.getPoint())
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(firstUnderZeroDetail.getMemberPointId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(firstFillUnderZeroDetail, firstFillUnderZeroExpected);

        // 두 번째 마이너스 포인트 채움 검증
        MemberPointDetail secondFillUnderZeroPointDetail = details.get(1);
        MemberPointDetail secondFillUnderZeroPointExpected = MemberPointDetail.builder()
                .point(-secondUnderZeroDetail.getPoint())
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(secondUnderZeroDetail.getMemberPointId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(secondFillUnderZeroPointDetail, secondFillUnderZeroPointExpected);

        // 나머지 포인트는 적립 포인트 검증
        MemberPointDetail upperZeroPointActual = details.get(2);
        MemberPointDetail upperZeroPointExpected = MemberPointDetail.builder()
                .point(earnPoint + currentPoint)
                .state(STATE_EARNED_POINT)
                .parentId(earnMemberPoint.getId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(upperZeroPointActual, upperZeroPointExpected);

        // 채운 마이너스 포인트는 부모 세팅
        assertThat(firstUnderZeroDetail.getParentId()).isEqualTo(firstUnderZeroDetail.getMemberPointId());
        assertThat(secondUnderZeroDetail.getParentId()).isEqualTo(secondUnderZeroDetail.getMemberPointId());
    }
}


