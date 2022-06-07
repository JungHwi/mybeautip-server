package com.jocoos.mybeautip.domain.member.service.social;

import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import com.jocoos.mybeautip.member.FacebookMember;
import com.jocoos.mybeautip.member.FacebookMemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FacebookSocialMemberService implements SocialMemberService<FacebookMember> {

    private final FacebookMemberRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String socialId) {
        return repository.existsByFacebookId(socialId);
    }

    @Override
    @Transactional(readOnly = true)
    public FacebookMember findById(String id) {
        return repository.getByFacebookId(id);
    }

    @Override
    @Transactional
    public FacebookMember save(SignupRequest request, long memberId) {
        FacebookMember facebookMember = repository.findById(request.getSocialId())
                .orElse(new FacebookMember(request, memberId));
        facebookMember.setMemberId(memberId);
        return repository.save(facebookMember);
    }
}