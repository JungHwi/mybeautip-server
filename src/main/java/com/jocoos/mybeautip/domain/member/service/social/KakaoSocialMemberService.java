package com.jocoos.mybeautip.domain.member.service.social;

import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import com.jocoos.mybeautip.member.KakaoMember;
import com.jocoos.mybeautip.member.KakaoMemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoSocialMemberService implements SocialMemberService<KakaoMember> {

    private final KakaoMemberRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String socialId) {
        return repository.existsByKakaoId(socialId);
    }

    @Override
    @Transactional(readOnly = true)
    public KakaoMember findById(String id) {
        return repository.getByKakaoId(id);
    }

    @Override
    @Transactional
    public KakaoMember save(SignupRequest request, long memberId) {
        KakaoMember kakaoMember = repository.findById(request.getSocialId())
                .orElse(new KakaoMember(request, memberId));
        kakaoMember.setMemberId(memberId);
        return repository.save(kakaoMember);
    }
}
