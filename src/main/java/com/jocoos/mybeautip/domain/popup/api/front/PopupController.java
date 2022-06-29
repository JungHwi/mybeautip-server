package com.jocoos.mybeautip.domain.popup.api.front;

import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.domain.popup.service.PopupService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PopupController {

    private final PopupService popupService;
    private final LegacyMemberService legacyMemberService;

    @GetMapping("/1/popup")
    public ResponseEntity<List<PopupResponse>> getPopupList() {

        Member member = legacyMemberService.currentMember();
        return ResponseEntity.ok(popupService.getPopup(member));
    }
}
