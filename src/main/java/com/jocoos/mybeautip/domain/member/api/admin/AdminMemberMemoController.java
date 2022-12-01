package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.domain.member.dto.MemoRequest;
import com.jocoos.mybeautip.domain.member.dto.MemoResponse;
import com.jocoos.mybeautip.domain.member.service.AdminMemberMemoService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminMemberMemoController {

    private final AdminMemberMemoService service;

    @PostMapping("/member/{memberId}/memo")
    public ResponseEntity<MemoResponse> writeMemo(@CurrentMember MyBeautipUserDetails currentMember,
                                                  @PathVariable Long memberId,
                                                  @RequestBody @Valid MemoRequest request) {
        return ResponseEntity.ok(service.write(request.content(), memberId, currentMember.getMember()));
    }

    @PatchMapping("/member/{memberId}/memo/{memoId}")
    public ResponseEntity<IdDto> editMemo(@CurrentMember MyBeautipUserDetails currentMember,
                                          @PathVariable Long memberId,
                                          @PathVariable Long memoId,
                                          @RequestBody @Valid MemoRequest request) {
        return ResponseEntity.ok(new IdDto(service.edit(memoId, memberId, request.content(), currentMember.getMember())));
    }

    @DeleteMapping("/member/{memberId}/memo/{memoId}")
    public ResponseEntity<IdDto> deleteMemo(@CurrentMember MyBeautipUserDetails currentMember,
                                            @PathVariable Long memberId,
                                            @PathVariable Long memoId) {
        return ResponseEntity.ok(new IdDto(service.delete(memoId, memberId, currentMember.getMember())));
    }
}
