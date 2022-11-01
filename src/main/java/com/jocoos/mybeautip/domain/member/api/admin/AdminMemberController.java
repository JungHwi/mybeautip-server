package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.domain.member.dto.AdminMemberDetailResponse;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import com.jocoos.mybeautip.domain.member.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @GetMapping("/member")
//    public ResponseEntity<PageResponse<AdminMemberResponse>> getMembers(
//            @RequestParam(required = false) MemberStatus status,
//            @RequestParam(required = false, defaultValue = "1") Long page,
//            @RequestParam(required = false, defaultValue = "10") Long size,
//            @RequestParam(required = false) GrantType grantType,
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt) {
//        return ResponseEntity.ok(new PageResponse<>(service.getMembers()));
//    }


    @GetMapping("/member/{memberId}")
    public ResponseEntity<AdminMemberDetailResponse> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(service.getMember(memberId));
    }
}
