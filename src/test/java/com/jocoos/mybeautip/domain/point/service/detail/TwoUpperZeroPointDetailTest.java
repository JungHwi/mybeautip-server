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

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_UNDER_ZERO_POINT;
import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_USE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[MemberPointDetailService] - 적립 포인트 2개 있을 때 테스트")
class TwoUpperZeroPointDetailTest extends DefaultMemberPointDetailServiceTest {


    @Autowired
    private MemberPointDetailService sut;

    private int currentPoint;
    private MemberPoint firstEarnMemberPoint;
    private MemberPoint secondEarnMemberPoint;

    @PostConstruct
    void postConstruct() {
        super.init();
    }

    @BeforeEach
    void init() {
        firstEarnMemberPoint = super.setInitUpperZeroPoint();
        secondEarnMemberPoint = super.setInitUpperZeroPoint();
        currentPoint = firstEarnMemberPoint.getPoint() + secondEarnMemberPoint.getPoint();
    }

    @DisplayName("회수 : 2개 적립 포인트 -> 적립 포인트 1개 소모하고 적립 포인트 1개 다 소모못함")
    @Test
    void alreadyEarnTwoPointUsedJustOnlyOne() {

        // given
        final int retrievePoint = firstEarnMemberPoint.getPoint() + secondEarnMemberPoint.getPoint() / 2;
        MemberPoint retrieveMemberPoint = super.createRetrieveMemberPoint(retrievePoint);

        // when
        List<MemberPointDetail> details = sut.retrievePoints(retrieveMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(2);
        details.sort(Comparator.comparingLong(MemberPointDetail::getId));

        // 첫 적립 포인트 소모 검증
        MemberPointDetail firstConsumeActual = details.get(0);
        MemberPointDetail firstConsumeExpected = MemberPointDetail.builder()
                .point(-firstEarnMemberPoint.getPoint())
                .state(STATE_USE_POINT)
                .parentId(firstEarnMemberPoint.getId())
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(firstEarnMemberPoint.getExpiryAt())
                .build();
        assertEquals(firstConsumeActual, firstConsumeExpected);

        // 나머지 포인트 소모 못하고 저장 검증
        MemberPointDetail remainingActual = details.get(1);
        MemberPointDetail remainingExpected = MemberPointDetail.builder()
                .point(firstEarnMemberPoint.getPoint() - retrievePoint)
                .state(STATE_USE_POINT)
                .parentId(secondEarnMemberPoint.getId())
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(secondEarnMemberPoint.getExpiryAt())
                .build();
        assertEquals(remainingActual, remainingExpected);

    }


    @DisplayName("회수 : 2개 적립 포인트 -> 적립 포인트 모두 소모하고 나머지 마이너스 포인트")
    @Test
    void TwoEarnPointAllUSed() {

        // given
        final int retrievePoint = currentPoint + 10;
        MemberPoint retrieveMemberPoint = createRetrieveMemberPoint(retrievePoint);

        // when
        List<MemberPointDetail> details = sut.retrievePoints(retrieveMemberPoint, currentPoint);

        // then
        assertThat(details).hasSize(3);
        details.sort(Comparator.comparingLong(MemberPointDetail::getId));

        MemberPointDetail firstConsumeActual = details.get(0);
        MemberPointDetail firstConsumeExpected = MemberPointDetail.builder()
                .point(-firstEarnMemberPoint.getPoint())
                .state(STATE_USE_POINT)
                .parentId(firstEarnMemberPoint.getId())
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(firstEarnMemberPoint.getExpiryAt())
                .build();
        assertEquals(firstConsumeActual, firstConsumeExpected);

        // 두번째 적립 포인트 소모 검증
        MemberPointDetail secondConsumeActual = details.get(1);
        MemberPointDetail secondConsumeExpected = MemberPointDetail.builder()
                .point(-secondEarnMemberPoint.getPoint())
                .state(STATE_USE_POINT)
                .parentId(secondEarnMemberPoint.getId())
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(secondEarnMemberPoint.getExpiryAt())
                .build();
        assertAll("두번째 + 포인트 소모", () -> assertEquals(secondConsumeActual, secondConsumeExpected));

        // 나머지 포인트는 마이너스 포인트로 검증
        MemberPointDetail underZeroActual = details.get(2);
        MemberPointDetail underZeroExpected = MemberPointDetail.builder()
                .point(currentPoint - retrievePoint)
                .state(STATE_UNDER_ZERO_POINT)
                .parentId(null)
                .memberPointId(retrieveMemberPoint.getId())
                .expiryAt(null)
                .build();
        assertAll("나머지는 마이너스 포인트", () -> assertEquals(underZeroActual, underZeroExpected));
    }
}

