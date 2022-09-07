package com.jocoos.mybeautip.domain.point.service.detail;

import com.jocoos.mybeautip.domain.point.service.MemberPointDetailService;
import com.jocoos.mybeautip.domain.point.service.detail.config.MemberPointDetailTestConfig;
import com.jocoos.mybeautip.global.config.jpa.DataSourceConfiguration;
import com.jocoos.mybeautip.global.util.TestMemberUtil;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(classes = {DataSourceConfiguration.class, MemberPointDetailTestConfig.class})
@DataJpaTest
public abstract class DefaultMemberPointDetailServiceTest {

    @Autowired
    private MemberPointDetailService memberPointDetailService;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    protected TestMemberUtil testMemberUtil;

    private final int initPoint = 0;

    @BeforeAll
    void init() {
        testMemberUtil.defaultTestSetting();
    }

    @AfterAll
    void afterAll() {
        testMemberUtil.defaultTestEnd();
    }

    protected MemberPointDetail setPartlyFillUnderZeroPoint(final int retrievePoint,
                                                            final int firstEarnPoint,
                                                            final int secondEarnPoint) {
        setEarnPoint(firstEarnPoint, initPoint);
        MemberPoint memberPoint = createRetrieveMemberPoint(retrievePoint);
        MemberPointDetail underZeroDetail =
                memberPointDetailService.retrievePoints(memberPoint, initPoint + firstEarnPoint).get(1);
        setEarnPoint(secondEarnPoint, initPoint + firstEarnPoint - retrievePoint);
        return underZeroDetail;
    }

    protected MemberPoint setPartlyConsumeUpperZeroPoint(final int earnPoint,
                                                         final int firstRetrievePoint,
                                                         final int secondRetrievePoint) {
        setRetrievePoint(firstRetrievePoint, initPoint);
        MemberPoint earnMemberPoint = setEarnPoint(earnPoint, initPoint - firstRetrievePoint);
        setRetrievePoint(secondRetrievePoint, initPoint - firstRetrievePoint + earnPoint);
        return earnMemberPoint;
    }

    protected MemberPoint setInitUpperZeroPoint() {
        final int earlyEarnPoint = 50;
        return setEarnPoint(earlyEarnPoint, initPoint);
    }

    protected MemberPointDetail setInitUnderZeroPoint() {
        final int earlyRetrievePoint = 50;
        return setRetrievePoint(earlyRetrievePoint, initPoint);
    }

    protected MemberPoint setEarnPoint(int earnPoint, int currentPoint) {
        MemberPoint memberPoint = createEarnMemberPoint(earnPoint);
        memberPointDetailService.earnPoints(memberPoint, currentPoint);
        return memberPoint;
    }

    protected MemberPointDetail setRetrievePoint(int retrievePoint, int currentPoint) {
        MemberPoint memberPoint = createRetrieveMemberPoint(retrievePoint);
        return memberPointDetailService.retrievePoints(memberPoint, currentPoint).get(0);
    }

    protected MemberPoint createRetrieveMemberPoint(int retrievePoint) {
        MemberPoint retrieveMemberPoint = memberPointRepository.save(
                MemberPoint.builder()
                .member(testMemberUtil.getMember())
                .point(retrievePoint)
                .build());
        return memberPointRepository.save(retrieveMemberPoint);
    }


    protected MemberPoint createEarnMemberPoint(int earnPoint) {
        MemberPoint memberPoint = MemberPoint.builder()
                .member(testMemberUtil.getMember())
                .point(earnPoint)
                .expiryAt(Timestamp.valueOf(LocalDateTime.now().plusYears(1))).build();
        return memberPointRepository.save(memberPoint);
    }

    protected static void assertEquals(MemberPointDetail o1, MemberPointDetail o2) {
        assertAll(
                () -> assertThat(o1.getPoint()).isEqualTo(o2.getPoint()),
                () -> assertThat(o1.getState()).isEqualTo(o2.getState()),
                () -> {
                    if (o1.getParentId() == null && o2.getParentId() == null) {
                        return;
                    }
                    assertThat(o1.getParentId()).isEqualTo(o2.getParentId());
                },
                () -> assertThat(o1.getMemberPointId()).isEqualTo(o2.getMemberPointId()),
                () -> {
                    if (o1.getExpiryAt() == null && o2.getExpiryAt() == null) {
                        return;
                    }
                    assertThat(o1.getExpiryAt()).isEqualToIgnoringSeconds(o2.getExpiryAt());
                });
    }
}
