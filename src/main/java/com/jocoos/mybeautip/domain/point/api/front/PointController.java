package com.jocoos.mybeautip.domain.point.api.front;

import com.jocoos.mybeautip.domain.point.code.PointStatus;
import com.jocoos.mybeautip.domain.point.dto.PointHistoryResponse;
import com.jocoos.mybeautip.domain.point.service.PointService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final LegacyMemberService legacyMemberService;

    @GetMapping("/1/point/history")
    public ResponseEntity getPointHistory(@RequestParam(required = false) PointStatus pointStatus,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestParam(defaultValue = MAX_LONG_STRING) long cursor) {

        Long memberId = legacyMemberService.currentMemberId();
        PointHistoryResponse result = pointService.getPointHistory(memberId, pointStatus, size, cursor);

        return ResponseEntity.ok(result);
    }
}
