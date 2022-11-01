package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.domain.member.dto.AdminMemberDetailResponse;
import com.jocoos.mybeautip.domain.member.dto.AdminMemberPointResponse;
import com.jocoos.mybeautip.domain.member.dto.AdminMemoRequest;
import com.jocoos.mybeautip.domain.member.dto.AdminMemberReportResponse;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import com.jocoos.mybeautip.domain.member.service.AdminMemberService;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminMemberController {

    private final AdminMemberService service;

    @GetMapping("/member/status")
    public ResponseEntity<List<MemberStatusResponse>> getStatusesWithCount() {
        return ResponseEntity.ok(service.getStatusesWithCount());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<AdminMemberDetailResponse> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(service.getMember(memberId));
    }

    @GetMapping("/member/{memberId}/point")
    public ResponseEntity<PageResponse<AdminMemberPointResponse>> getMemberPointHistory(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getPointHistory(memberId, PageRequest.of(page - 1, size)));
    }

    @GetMapping("/member/{memberId}/report")
    public ResponseEntity<PageResponse<AdminMemberReportResponse>> getMemberReportHistory(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getReportHistory(memberId, PageRequest.of(page - 1, size)));
    }

    @PatchMapping("/member/{memberId}/memo")
    public ResponseEntity<IdDto> updateMemo(@PathVariable Long memberId, @RequestBody AdminMemoRequest request) {
        return ResponseEntity.ok(new IdDto(service.updateMemo(memberId, request.memo())));
    }
}
