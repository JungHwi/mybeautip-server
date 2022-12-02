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

@DisplayName("[MemberPointDetailService] - 일부만 남은 포인트 테스트")
class PartPointDetailTest extends DefaultMemberPointDetailServiceTest {

    @Autowired
    private MemberPointDetailService sut;

    @DisplayName("적립 : 이미 마이너스 포인트 중 일부는 채워진 상태 -> 포인트 0 이상으로")
    @Test
    void alreadyEarnUnderZeroPoint() {

        // given
        final int initEarnPoint = 10;
        final int earlyRetrievePoint = 50;
        final int earlyEarnPoint = 30;
        MemberPointDetail earlyRetrieveDetail =
                super.setPartlyFillUnderZeroPoint(earlyRetrievePoint, initEarnPoint, earlyEarnPoint);

        final int currentPoint = -earlyRetrievePoint + initEarnPoint + earlyEarnPoint;

        final int earnPoint = -currentPoint + 10;
        MemberPoint earnMemberPoint = super.createEarnMemberPoint(earnPoint);


        // when
        List<MemberPointDetail> details = sut.earnPoints(earnMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(2);
        details.sort(Comparator.comparingLong(MemberPointDetail::getId));

        // 이미 일부 채워진 마이너스 포인트 채움 검증
        MemberPointDetail fillUnderZeroActual = details.get(0);
        MemberPointDetail fillUnderZeroExpected = MemberPointDetail.builder()
                .point(-currentPoint)
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(earlyRetrieveDetail.getMemberPointId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(fillUnderZeroActual, fillUnderZeroExpected);

        // 나머지 적립 검증
        MemberPointDetail upperZeroActual = details.get(1);
        MemberPointDetail upperZeroExpected = MemberPointDetail.builder()
                .point(earnPoint + currentPoint)
                .state(STATE_EARNED_POINT)
                .parentId(earnMemberPoint.getId())
                .memberPointId(earnMemberPoint.getId())
                .expiryAt(earnMemberPoint.getExpiryAt())
                .build();
        assertEquals(upperZeroActual, upperZeroExpected);

        // 채워진 마이너스 포인트 부모 설정 검증
        assertThat(earlyRetrieveDetail.getParentId()).isEqualTo(earlyRetrieveDetail.getMemberPointId());
    }

    @DisplayName("회수 : 이미 적립된 포인트 중 일부는 마이너스 포인트 채우는데 일부는 사용한 상태 -> 마이너스 포인트로")
    @Test
    void alreadyUsedEarnPointTest() {

        // given
        final int initRetrievePoint = 10;
        final int earlyEarnPoint = 50;
        final int earlyRetrievePoint = 30;
        MemberPoint earlyEarnMemberPoint =
                super.setPartlyConsumeUpperZeroPoint(earlyEarnPoint, initRetrievePoint, earlyRetrievePoint);

        final int currentPoint = earlyEarnPoint - initRetrievePoint - earlyRetrievePoint;

        final int retrievePoint = currentPoint + 10;
        MemberPoint retrieveMemberPoint = super.createRetrieveMemberPoint(retrievePoint);

        // when
        List<MemberPointDetail> details = sut.retrievePoints(retrieveMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(2);
        details.sort(Comparator.comparingInt(MemberPointDetail::getState));

        // 남은 적립 포인트 소모 검증
        MemberPointDetail consumeUpperZeroActual = details.get(0);
        MemberPointDetail consumeUpperZeroExpected = MemberPointDetail.builder()
                .point(-currentPoint)
                .state(STATE_USE_POINT)
                .parentId(earlyEarnMemberPoint.getId())
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(earlyEarnMemberPoint.getExpiryAt())
                .build();
        assertEquals(consumeUpperZeroActual, consumeUpperZeroExpected);

        // 나머지는 마이너스 포인트로 검증
        MemberPointDetail underZeroActual = details.get(1);
        MemberPointDetail underZeroExpected = MemberPointDetail.builder()
                .point(currentPoint - retrievePoint)
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(null)
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(null)
                .build();
        assertEquals(underZeroActual, underZeroExpected);
    }
}
