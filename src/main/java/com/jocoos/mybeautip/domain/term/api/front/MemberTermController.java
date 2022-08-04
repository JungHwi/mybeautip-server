package com.jocoos.mybeautip.domain.term.api.front;


import com.jocoos.mybeautip.domain.term.dto.*;
import com.jocoos.mybeautip.domain.term.service.MemberTermService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.jocoos.mybeautip.domain.term.dto.MemberTermRequest.getTermIds;


@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class MemberTermController {

    private final MemberTermService memberTermService;
    private final LegacyMemberService legacyMemberService;

//    @PostMapping("/1/members/me/terms")
    public ResponseEntity<List<MemberTermResponse>> chooseTerms(
            @RequestBody List<MemberTermRequest> requests) {
        return ResponseEntity.ok(memberTermService.chooseTerms(getTermIds(requests)));
    }

//    @GetMapping("/1/members/me/terms/change")
    public ResponseEntity<List<TermResponse>> getChangedTerms() {
        return ResponseEntity.ok(memberTermService.getChangedTerms(legacyMemberService.currentMemberId()));
    }

//    @PatchMapping("/1/members/me/terms/change")
    public ResponseEntity<List<MemberTermResponse>> chooseChangedTerms(
            @Valid @RequestBody List<MemberTermRequest> requests) {
        return ResponseEntity
                .ok(memberTermService.chooseUpdateTerms(getTermIds(requests), legacyMemberService.currentMemberId()));
    }

//    @PatchMapping("/1/members/me/terms/{termId}/agree")
    public ResponseEntity<MemberTermResponse> changeOptionalTermTrue(@PathVariable long termId) {
        return ResponseEntity
                .ok(memberTermService.changeOptionalTerm(termId, legacyMemberService.currentMemberId(), true));
    }

//    @PatchMapping("/1/members/me/terms/{termId}/disagree")
    public ResponseEntity<MemberTermResponse> changeOptionalTermFalse(@PathVariable long termId) {
        return ResponseEntity
                .ok(memberTermService.changeOptionalTerm(termId, legacyMemberService.currentMemberId(), false));
    }


    // 2021-07-28 현재 서버에서 약관 관리하지 않기 때문에 임시로 만듦, 추후 서버에서 약관 관리한다면 삭제
    @PatchMapping("/1/members/me/terms/option/change")
    public ResponseEntity<TermTypeResponse> changeOptionalTermByType(@RequestBody TermTypeRequest request) {
        return ResponseEntity
                .ok(memberTermService.changeOptionalTermByType(
                        request.getTermType(),
                        legacyMemberService.currentMemberId(),
                        request.getIsAccept()));
    }
}
