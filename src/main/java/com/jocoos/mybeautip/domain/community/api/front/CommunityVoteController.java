package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityVoteMemberResponse;
import com.jocoos.mybeautip.domain.community.service.CommunityVoteService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CommunityVoteController {

    private final CommunityVoteService communityVoteService;
    private final LegacyMemberService legacyMemberService;

    @PatchMapping("/1/community/{communityId}/vote/{voteId}")
    public ResponseEntity<CommunityVoteMemberResponse> vote(@PathVariable Long communityId, @PathVariable Long voteId) {
        Member member = legacyMemberService.currentMember();
        return ResponseEntity.ok(communityVoteService.vote(member, communityId, voteId));
    }
}
