package com.jocoos.mybeautip.domain.zzz_test.api;

import com.jocoos.mybeautip.domain.zzz_test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// FIXME 꼭 지우기 바람!! 현재는 서비스 개발이 운영보다 중요하다고 판단하여 Test Package 만듬.
// FIXME 지울 때 ResourceServerConfig 에서 /test/** 권한 열려 있는 것도 함께 삭제.
@Profile("!production")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    @PatchMapping("/member/{memberId}/dormant")
    public ResponseEntity toDormantTest(@PathVariable long memberId) {
        testService.toDormantMember(memberId);
        return new ResponseEntity(HttpStatus.OK);
    }
}