package com.jocoos.mybeautip.domain.member.api.batch;

import com.jocoos.mybeautip.domain.member.service.AdminMemberService;
import com.jocoos.mybeautip.domain.member.service.DormantMemberNotificationService;
import com.jocoos.mybeautip.domain.member.service.DormantMemberService;
import com.jocoos.mybeautip.global.dto.single.IntegerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchMemberController {

    private final AdminMemberService service;
    private final DormantMemberService dormantMemberService;
    private final DormantMemberNotificationService dormantMemberNotificationService;

    @PatchMapping("/member/dormant")
    public ResponseEntity<IntegerDto> changeDormantMember() {
        int result = dormantMemberService.changeDormantMember();
        return new ResponseEntity<>(new IntegerDto(result), HttpStatus.OK);
    }

    @PostMapping("/member/dormant/notification")
    public ResponseEntity<IntegerDto> dormantNotification() {
        int result = dormantMemberNotificationService.send();
        return ResponseEntity.ok(new IntegerDto(result));
    }

    @PatchMapping("/member/suspended")
    public ResponseEntity<IntegerDto> offSuspendedMember() {
        int result = service.offSuspendedMember();
        return new ResponseEntity<>(new IntegerDto(result), HttpStatus.OK);
    }

    @DeleteMapping("/refresh-token")
    public ResponseEntity<IntegerDto> deleteExpiredRefreshToken() {
        int result = service.deleteExpiredRefreshToken();
        return new ResponseEntity<>(new IntegerDto(result), HttpStatus.OK);
    }

}
