package com.jocoos.mybeautip.domain.member.api.front;


import com.jocoos.mybeautip.domain.member.dto.MemberInvitationInfoResponse;
import com.jocoos.mybeautip.domain.member.service.MemberInvitationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class MemberInvitationInfoController {

    private final MemberInvitationInfoService memberInvitationInfoService;

    @GetMapping("/1/member/invitation-info")
    public ResponseEntity<MemberInvitationInfoResponse> getMemberInvitationInfo() {
        return ResponseEntity.ok(memberInvitationInfoService.getMemberInvitationInfo());
    }

    @GetMapping("/1/members/invitation-info")
    public ResponseEntity<MemberInvitationInfoResponse> getMemberInvitationInfoLegacy() {
        return ResponseEntity.ok(memberInvitationInfoService.getMemberInvitationInfo());
    }
}
