package com.jocoos.mybeautip.domain.member.service.social;

import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import com.jocoos.mybeautip.member.AppleMember;
import com.jocoos.mybeautip.member.AppleMemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppleSocialMemberService implements SocialMemberService<AppleMember> {

    private final AppleMemberRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String socialId) {
        return repository.existsByAppleId(socialId);
    }

    @Override
    @Transactional(readOnly = true)
    public AppleMember findById(String id) {
        return repository.getByAppleId(id);
    }

    @Override
    @Transactional
    public AppleMember save(SignupRequest request, long memberId) {
        AppleMember appleMember = repository.findById(request.getSocialId())
                .orElse(new AppleMember(request, memberId));
        appleMember.setMemberId(memberId);
        return repository.save(appleMember);
    }
}