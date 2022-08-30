package com.jocoos.mybeautip.domain.point.api.front;

import com.jocoos.mybeautip.domain.point.code.PointStatusGroup;
import com.jocoos.mybeautip.domain.point.dto.PointHistoryResponse;
import com.jocoos.mybeautip.domain.point.dto.PointMonthlyStatisticsResponse;
import com.jocoos.mybeautip.domain.point.service.PointService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.restapi.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final LegacyMemberService legacyMemberService;

    @GetMapping("/1/point/statistics/monthly")
    public ResponseEntity getPointHistory() {

        Long memberId = legacyMemberService.currentMemberId();
        PointMonthlyStatisticsResponse result = pointService.getPointMonthlyHistory(memberId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/1/point/history")
    public ResponseEntity getPointHistory(@RequestParam(name = "point_status_group", required = false) PointStatusGroup pointStatusGroup,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestParam(defaultValue = MAX_LONG_STRING) long cursor) {

        Long memberId = legacyMemberService.currentMemberId();
        List<PointHistoryResponse> result = pointService.getPointHistoryList(memberId, pointStatusGroup, size, cursor);

        CursorResponse cursorResponse = new CursorResponse.Builder<>("/api/1/point/history", result)
                .withCount(size)
                .withCursor(result.size() > 0 ? String.valueOf(result.get(result.size() - 1).getId()) : null)
                .toBuild();

        return ResponseEntity.ok(cursorResponse);
    }
}
