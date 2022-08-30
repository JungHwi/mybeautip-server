package com.jocoos.mybeautip.domain.member.service.social;

import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.AppleMember;
import com.jocoos.mybeautip.member.AppleMemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
        if (StringUtils.isBlank(request.getRefreshToken())) {
            throw new BadRequestException("token_is_null", "Refresh token must be not null.");
        }

        AppleMember appleMember = repository.findById(request.getSocialId())
                .orElse(new AppleMember(request, memberId));
        appleMember.setMemberId(memberId);
        appleMember.setRefreshToken(request.getRefreshToken());
        return repository.save(appleMember);
    }
}