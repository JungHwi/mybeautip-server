package com.jocoos.mybeautip.domain.point.service.detail;

import com.jocoos.mybeautip.domain.point.service.MemberPointDetailService;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static com.jocoos.mybeautip.member.point.MemberPoint.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[MemberPointDetailService] - 기본 동작 테스트")
class SimplePointDetailServiceTest extends DefaultMemberPointDetailServiceTest {

    @Autowired
    private MemberPointDetailService sut;

    private final int initPoint = 0;

    @DisplayName("적립 : 0 포인트 -> + 포인트")
    @Test
    void earnZeroToUpperZero() {

        // given
        final int earnPoint = 30;
        MemberPoint earnMemberPoint = super.createEarnMemberPoint(earnPoint);

        // when
        List<MemberPointDetail> details = sut.earnPoints(earnMemberPoint, initPoint);

        //then
        assertThat(details).hasSize(1);
        MemberPointDetail earnDetailActual = details.get(0);
        MemberPointDetail earnDetailExpected = MemberPointDetail.builder()
                .point(earnPoint)
                .state(STATE_EARNED_POINT)
                .parentId(earnMemberPoint.getId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(earnDetailActual, earnDetailExpected);
    }

    @DisplayName("회수 : 0 포인트 -> - 포인트")
    @Test
    void useZeroToUnderZero() {

        // given
        final int retrievePoint = 30;
        MemberPoint retrieveMemberPoint = super.createRetrieveMemberPoint(retrievePoint);

        // when
        List<MemberPointDetail> details = sut.retrievePoints(retrieveMemberPoint, initPoint);

        //then
        assertThat(details).hasSize(1);
        MemberPointDetail retrieveDetailActual = details.get(0);
        MemberPointDetail retrieveDetailExpected = MemberPointDetail.builder()
                .point(-retrievePoint)
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(null)
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(null)
                .build();
        assertEquals(retrieveDetailActual, retrieveDetailExpected);
    }

    @DisplayName("적립 : + 포인트 -> + 포인트")
    @Test
    void earnUpperZeroToUpperZero() {

        // given
        MemberPoint earlyEarnMemberPoint = super.setInitUpperZeroPoint();

        final int currentPoint = earlyEarnMemberPoint.getPoint();
        final int earnPoint = currentPoint / 2;
        MemberPoint earnMemberPoint = super.createEarnMemberPoint(earnPoint);

        // when
        List<MemberPointDetail> details = sut.earnPoints(earnMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(1);
        MemberPointDetail earnDetailActual = details.get(0);
        MemberPointDetail earnDetailExpected = MemberPointDetail.builder()
                .point(earnPoint)
                .state(STATE_EARNED_POINT)
                .parentId(earnMemberPoint.getId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(earnDetailActual, earnDetailExpected);
    }

    @DisplayName("회수 : + 포인트 -> + 포인트")
    @Test
    void useUpperZeroToUpperZero() {

        // given
        MemberPoint earlyEarnMemberPoint = super.setInitUpperZeroPoint();

        final int currentPoint = earlyEarnMemberPoint.getPoint();
        final int retrievePoint = currentPoint / 2;
        MemberPoint retrieveMemberPoint = createRetrieveMemberPoint(retrievePoint);

        // when
        List<MemberPointDetail> details = sut.retrievePoints(retrieveMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(1);
        MemberPointDetail retrieveDetailActual = details.get(0);
        MemberPointDetail retrieveDetailExpected = MemberPointDetail.builder()
                .point(-retrievePoint)
                .state(STATE_USE_POINT)
                .parentId(earlyEarnMemberPoint.getId())
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(earlyEarnMemberPoint.getExpiryAt())
                .build();
        assertEquals(retrieveDetailActual, retrieveDetailExpected);
    }

    @DisplayName("적립 : - 포인트 -> - 포인트")
    @Test
    void earnUnderZeroToUnderZero() {

        // given
        MemberPointDetail earlyRetrievePoint = super.setInitUnderZeroPoint();
        final int currentPoint = earlyRetrievePoint.getPoint();

        final int earnPoint = -currentPoint / 2;
        MemberPoint earnMemberPoint = super.createEarnMemberPoint(earnPoint);

        // when
        List<MemberPointDetail> details = sut.earnPoints(earnMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(1);

        MemberPointDetail earnDetailActual = details.get(0);
        MemberPointDetail earnDetailExpected = MemberPointDetail.builder()
                .point(earnPoint)
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(earlyRetrievePoint.getMemberPointId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(earnDetailActual, earnDetailExpected);
    }

    @DisplayName("회수 : - 포인트 -> - 포인트")
    @Test
    void useUnderZeroToUnderZero() {

        // given
        MemberPointDetail earlyRetrievePoint = super.setInitUnderZeroPoint();
        final int currentPoint = earlyRetrievePoint.getPoint();

        final int retrievePoint = -currentPoint + 1;
        MemberPoint retrieveMemberPoint = super.createRetrieveMemberPoint(retrievePoint);

        // when
        List<MemberPointDetail> details = sut.retrievePoints(retrieveMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(1);

        MemberPointDetail retrieveDetailActual = details.get(0);
        MemberPointDetail retrieveDetailExpected = MemberPointDetail.builder()
                .point(-retrievePoint)
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(null)
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(null)
                .build();
        assertEquals(retrieveDetailActual, retrieveDetailExpected);
    }

    @DisplayName("적립 : - 포인트 -> + 포인트")
    @Test
    void earnUnderZeroToUpperZero() {

        // given
        MemberPointDetail earlyRetrieveMemberPoint = super.setInitUnderZeroPoint();

        final int currentPoint = earlyRetrieveMemberPoint.getPoint();
        final int earnPoint = -currentPoint + 10;
        MemberPoint earnMemberPoint = super.createEarnMemberPoint(earnPoint);

        // when
        List<MemberPointDetail> details = sut.earnPoints(earnMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(2);
        details.sort(Comparator.comparingLong(MemberPointDetail::getId));

        // 마이너스 포인트 채움 검증
        MemberPointDetail fillUnderZeroActual = details.get(0);
        MemberPointDetail fillUnderZeroExpected = MemberPointDetail.builder()
                .point(-earlyRetrieveMemberPoint.getPoint())
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(earlyRetrieveMemberPoint.getMemberPointId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(fillUnderZeroActual, fillUnderZeroExpected);

        // 나머지는 적립 포인트
        MemberPointDetail earnPointActual = details.get(1);
        MemberPointDetail earnPointExpected = MemberPointDetail.builder()
                .point(earnPoint + earlyRetrieveMemberPoint.getPoint())
                .state(STATE_EARNED_POINT)
                .parentId(earnMemberPoint.getId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(earnPointActual, earnPointExpected);
    }

    @DisplayName("회수 : + 포인트 -> - 포인트")
    @Test
    void useUpperZeroToUnderZero() {

        // given
        MemberPoint earlyEarnMemberPoint = super.setInitUpperZeroPoint();

        final int currentPoint = earlyEarnMemberPoint.getPoint();
        final int retrievePoint = currentPoint + 10;
        MemberPoint retrieveMemberPoint = super.createRetrieveMemberPoint(retrievePoint);

        // when
        List<MemberPointDetail> details = sut.retrievePoints(retrieveMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(2);
        details.sort(Comparator.comparingLong(MemberPointDetail::getId));

        // 적립 포인트 소모 검증
        MemberPointDetail consumeEarnDetailActual = details.get(0);
        MemberPointDetail consumeEarnDetailExpected = MemberPointDetail.builder()
                .point(-earlyEarnMemberPoint.getPoint())
                .state(STATE_USE_POINT)
                .parentId(earlyEarnMemberPoint.getId())
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(earlyEarnMemberPoint.getExpiryAt())
                .build();
        assertEquals(consumeEarnDetailActual, consumeEarnDetailExpected);

        // 나머지 회수 포인트는 마이너스 포인트 검증
        MemberPointDetail underZeroActual = details.get(1);
        MemberPointDetail underZeroExpected = MemberPointDetail.builder()
                .point(earlyEarnMemberPoint.getPoint() - retrievePoint)
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(null)
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(null)
                .build();
        assertEquals(underZeroActual, underZeroExpected);
    }
}
