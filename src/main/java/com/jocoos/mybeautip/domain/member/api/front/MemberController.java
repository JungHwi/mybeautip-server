package com.jocoos.mybeautip.domain.member.api.front;


import com.jocoos.mybeautip.domain.member.service.MemberService;
import com.jocoos.mybeautip.domain.member.service.social.DormantMemberService;
import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.global.dto.single.LongDto;
import com.jocoos.mybeautip.global.dto.single.StringDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;
    private final DormantMemberService dormantMemberService;

    @GetMapping("/1/member/random-username")
    public ResponseEntity<StringDto> getRandomUsername() {
        String randomUsername = service.generateUsername();
        return ResponseEntity.ok(new StringDto(randomUsername));
    }

    @PatchMapping("/1/member/wakeup")
    public ResponseEntity<PopupResponse> wakeup(@RequestBody LongDto memberId) {
        PopupResponse popup = dormantMemberService.wakeup(memberId.getNumber());
        return ResponseEntity.ok(popup);
    }
}


