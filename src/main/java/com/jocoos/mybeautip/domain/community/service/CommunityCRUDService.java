package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommunityCRUDService {

    private final LegacyMemberService legacyMemberService;
    private final CommunityDao communityDao;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public Community write(WriteCommunityRequest request) {
        Member member = legacyMemberService.currentMember();
        request.setMember(member);
        Community community = communityDao.write(request);
        awsS3Handler.copy(request.getFiles(), UrlDirectory.COMMUNITY.getDirectory(community.getId()));
        return community;
    }


}
