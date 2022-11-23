package com.jocoos.mybeautip.domain.member.api.front;


import com.jocoos.mybeautip.domain.member.service.MemberService;
import com.jocoos.mybeautip.global.dto.single.StringDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;

    @GetMapping("/1/member/random-username")
    public ResponseEntity<StringDto> getRandomUsername() {
        String randomUsername = service.generateUsername();
        return ResponseEntity.ok(new StringDto(randomUsername));
    }
}
