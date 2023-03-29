package com.jocoos.mybeautip.domain.member.service.activity.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.CommunityConvertService;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.domain.member.dto.MemberActivityRequest;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityResponse;
import com.jocoos.mybeautip.domain.member.service.activity.MyActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Long.MAX_VALUE;

@RequiredArgsConstructor
@Service
public class CommunityActivityService implements MyActivityService<MyCommunityResponse> {

    private final CommunityDao communityDao;
    private final CommunityConvertService convertService;

    @Override
    public MemberActivityType getType() {
        return MemberActivityType.COMMUNITY;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MyCommunityResponse> getMyActivity(MemberActivityRequest request) {
        long idCursor = request.idCursor() == null ? MAX_VALUE : request.idCursor();
        Pageable pageable = PageRequest.of(0, request.size(), Sort.by(Sort.Direction.DESC, "id"));
        List<Community> communityList = communityDao.get(request.member().getId(), idCursor, pageable);
        List<MyCommunityResponse> communityResponses = convertService.toMyCommunityResponse(communityList);

        for (MyCommunityResponse response : communityResponses) {
            response.changeContents();
        }

        return communityResponses;
    }
}
