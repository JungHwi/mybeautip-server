package com.jocoos.mybeautip.domain.point.service.detail.config;

import com.jocoos.mybeautip.domain.point.dao.MemberPointDetailDao;
import com.jocoos.mybeautip.domain.point.service.MemberPointDetailCalculateService;
import com.jocoos.mybeautip.domain.point.service.MemberPointDetailService;
import com.jocoos.mybeautip.global.util.TestMemberUtil;
import com.jocoos.mybeautip.member.point.MemberPointDetailRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@TestConfiguration
public class MemberPointDetailTestConfig {

    @Bean
    public TestMemberUtil testMemberUtil() {
        return new TestMemberUtil();
    }

    @Bean
    public MemberPointDetailService memberPointDetailService(MemberPointDetailDao memberPointDetailDao,
                                                             MemberPointDetailCalculateService memberPointDetailCalculateService) {
        return new MemberPointDetailService(memberPointDetailDao, memberPointDetailCalculateService);
    }

    @Bean
    public MemberPointDetailCalculateService memberPointDetailCalculateService(MemberPointDetailDao memberPointDetailDao,
                                                                               EntityManager entityManager) {
        return new MemberPointDetailCalculateService(memberPointDetailDao, entityManager);
    }

    @Bean
    public MemberPointDetailDao memberPointDetailDao(MemberPointDetailRepository memberPointDetailRepository) {
        return new MemberPointDetailDao(memberPointDetailRepository);
    }
}
