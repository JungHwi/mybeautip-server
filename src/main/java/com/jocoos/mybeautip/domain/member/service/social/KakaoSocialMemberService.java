package com.jocoos.mybeautip.domain.member.service.social;

import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import com.jocoos.mybeautip.member.KakaoMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KakaoSocialMemberService implements SocialMemberService {

    private final KakaoMemberRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String socialId) {
        return repository.existsByKakaoId(socialId);
    }
}
