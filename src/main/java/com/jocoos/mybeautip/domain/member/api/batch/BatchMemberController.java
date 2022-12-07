package com.jocoos.mybeautip.domain.member.api.batch;

import com.jocoos.mybeautip.domain.member.service.AdminMemberService;
import com.jocoos.mybeautip.domain.member.service.social.DormantMemberService;
import com.jocoos.mybeautip.global.dto.single.IntegerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchMemberController {

    private final AdminMemberService service;
    private final DormantMemberService dormantMemberService;

    @PatchMapping("/member/dormant")
    public ResponseEntity<IntegerDto> changeDormantMember() {
        int result = dormantMemberService.changeDormantMember();
        return new ResponseEntity<>(new IntegerDto(result), HttpStatus.OK);
    }

    @PatchMapping("/member/suspended")
    public ResponseEntity<IntegerDto> offSuspendedMember() {
        int result = service.offSuspendedMember();
        return new ResponseEntity<>(new IntegerDto(result), HttpStatus.OK);
    }
}
