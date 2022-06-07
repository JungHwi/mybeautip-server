package com.jocoos.mybeautip.domain.member.service.social;

import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import com.jocoos.mybeautip.member.NaverMember;
import com.jocoos.mybeautip.member.NaverMemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaverSocialMemberService implements SocialMemberService<NaverMember> {

    private final NaverMemberRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String socialId) {
        return repository.existsByNaverId(socialId);
    }

    @Override
    @Transactional(readOnly = true)
    public NaverMember findById(String id) {
        return repository.getByNaverId(id);
    }

    @Override
    @Transactional
    public NaverMember save(SignupRequest request, long memberId) {
        NaverMember naverMember = repository.findById(request.getSocialId())
                .orElse(new NaverMember(request, memberId));
        naverMember.setMemberId(memberId);
        return repository.save(naverMember);
    }
}
