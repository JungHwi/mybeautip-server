package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.domain.member.dto.MemoRequest;
import com.jocoos.mybeautip.domain.member.dto.MemoResponse;
import com.jocoos.mybeautip.domain.member.service.AdminMemberMemoService;
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
    public ResponseEntity<MemoResponse> writeMemo(MyBeautipUserDetails currentMember,
                                                  @PathVariable Long memberId,
                                                  @RequestBody @Valid MemoRequest request) {
        MemoResponse response = service.write(request.memo(), memberId, currentMember.getMember());
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/member/{memberId}/memo/{memoId}")
    public ResponseEntity<IdDto> editMemo(MyBeautipUserDetails currentMember,
                                          @PathVariable Long memberId,
                                          @PathVariable Long memoId,
                                          @RequestBody @Valid MemoRequest request) {
        return ResponseEntity.ok(new IdDto(service.edit(memoId, memberId, request.memo(), currentMember.getMember())));
    }

    @DeleteMapping("/member/{memberId}/memo/{memoId}")
    public ResponseEntity<IdDto> deleteMemo(MyBeautipUserDetails currentMember,
                                            @PathVariable Long memberId,
                                            @PathVariable Long memoId) {
        return ResponseEntity.ok(new IdDto(service.delete(memoId, memberId, currentMember.getMember())));
    }
}
