package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import com.jocoos.mybeautip.domain.member.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
