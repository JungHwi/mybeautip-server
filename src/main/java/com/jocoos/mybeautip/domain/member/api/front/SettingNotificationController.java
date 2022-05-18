package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.member.dto.SettingNotificationDto;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/2/member/setting/notification")
public class SettingNotificationController {

    private final MemberService memberService;

    @PutMapping
    public ResponseEntity<MemberInfo> setPushable(@RequestBody SettingNotificationDto request) {
        MemberInfo memberInfo = memberService.updateSettingNotification(memberService.currentMember(), request);

        return new ResponseEntity<>(memberInfo, HttpStatus.OK);
    }

}
