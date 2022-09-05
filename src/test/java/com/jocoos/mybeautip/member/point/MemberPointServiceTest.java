package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.util.MemberUtil;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.*;
import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_UNDER_ZERO_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class MemberPointServiceTest {

    @Autowired
    private MemberPointService memberPointService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberPointDetailRepository memberPointDetailRepository;

    private Member member;

    @BeforeAll
    void init() {
        final long memberId = 123L;
        final String socialId = "testSocialId";
        member = memberRepository.save(MemberUtil.defaultMember(memberId, socialId));
    }

    @Order(1)
    @DisplayName("회수 : 0 포인트 -> - 포인트")
    @Test
    void zeroToUnderZeroRetrieve() {
        final int originalMemberPoint = member.getPoint();
        final long domainId = 1L;
        final ActivityPointType type = DELETE_COMMUNITY;

        memberPointService.retrievePoints(type, domainId, this.member);

        MemberPoint memberPoint = getMemberPoint(domainId, type);

        Optional<MemberPointDetail> memberPointDetailOptional =
                memberPointDetailRepository.findTopByMemberIdOrderByIdDesc(member.getId());
        assertThat(memberPointDetailOptional).isPresent();
        MemberPointDetail memberPointDetail = memberPointDetailOptional.get();

        assertThat(memberPoint.getPoint()).isEqualTo(type.getPoint());
        assertThat(memberPointDetail.getPoint()).isEqualTo(-type.getPoint());
        assertThat(memberPointDetail.getParentId()).isNull();
        assertThat(memberPointDetail.getMemberPointId()).isEqualTo(memberPoint.getId());
        assertThat(memberPointDetail.getState()).isEqualTo(STATE_UNDER_ZERO_POINT);
        assertThat(member.getPoint()).isEqualTo(originalMemberPoint - type.getPoint());
        log.info("{}", member.getPoint());
        log.info("{}, {}, {}", memberPointDetail.getPoint(), memberPointDetail.getParentId(), memberPointDetail.getMemberPointId());
    }

    @Order(2)
    @DisplayName("회수 : - 포인트 -> - 포인트")
    @Test
    void underZeroToUnderZeroRetrieve() {
        final int originalMemberPoint = member.getPoint();
        final long domainId = 2L;
        final ActivityPointType type = DELETE_VIDEO_COMMENT;

        memberPointService.retrievePoints(type, domainId, member);

        MemberPoint memberPoint = getMemberPoint(domainId, type);

        Optional<MemberPointDetail> memberPointDetailOptional =
                memberPointDetailRepository.findTopByMemberIdOrderByIdDesc(member.getId());
        assertThat(memberPointDetailOptional).isPresent();
        MemberPointDetail memberPointDetail = memberPointDetailOptional.get();

        assertThat(memberPoint.getPoint()).isEqualTo(type.getPoint());
        assertThat(memberPointDetail.getPoint()).isEqualTo(-type.getPoint());
        assertThat(memberPointDetail.getParentId()).isNull();
        assertThat(memberPointDetail.getMemberPointId()).isEqualTo(memberPoint.getId());
        assertThat(memberPointDetail.getState()).isEqualTo(STATE_UNDER_ZERO_POINT);
        assertThat(member.getPoint()).isEqualTo(originalMemberPoint - type.getPoint());
        log.info("{}", member.getPoint());
        log.info("{}, {}, {}", memberPointDetail.getPoint(), memberPointDetail.getParentId(), memberPointDetail.getMemberPointId());
    }

    private MemberPoint getMemberPoint(long domainId, ActivityPointType type) {
        Optional<MemberPoint> memberPointOptional =
                memberPointRepository.findByMemberAndActivityTypeAndActivityDomainId(member, type, domainId);
        assertThat(memberPointOptional).isPresent();
        return memberPointOptional.get();
    }

    @Order(3)
    @DisplayName("적립 : - 포인트 -> - 포인트")
    @Test
    void underZeroToUnderZeroEarn() {
        final int originalMemberPoint = member.getPoint();
        final long domainId = 3L;
        final ActivityPointType type = WRITE_COMMUNITY_COMMENT;

        memberPointService.earnPoint(type, domainId, member);

        MemberPoint memberPoint = getMemberPoint(domainId, type);

        Optional<MemberPointDetail> memberPointDetailOptional =
                memberPointDetailRepository.findTopByMemberIdOrderByIdDesc(member.getId());
        assertThat(memberPointDetailOptional).isPresent();
        MemberPointDetail memberPointDetail = memberPointDetailOptional.get();
        log.info("{}", memberPointDetail.getPoint());

        List<MemberPointDetail> details
                = memberPointDetailRepository.findAllByMemberIdAndStateAndParentIdIsNullOrderByIdAsc(member.getId(), STATE_UNDER_ZERO_POINT);
        assertThat(details).hasSize(1);
        MatcherAssert.assertThat(details, contains(
                hasProperty("state", is(STATE_UNDER_ZERO_POINT)),
                hasProperty("parentId", is(null))
        ));
        Long underZeroId = details.get(0).getId();

        assertThat(memberPoint.getPoint()).isEqualTo(type.getPoint());
        assertThat(memberPointDetail.getPoint()).isEqualTo(type.getPoint());
        assertThat(memberPointDetail.getParentId()).isEqualTo(underZeroId);
        assertThat(memberPointDetail.getMemberPointId()).isEqualTo(memberPoint.getId());
        assertThat(memberPointDetail.getState()).isEqualTo(STATE_UNDER_ZERO_POINT);
        assertThat(member.getPoint()).isEqualTo(originalMemberPoint + type.getPoint());

    }

}
