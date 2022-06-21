package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.dto.EventJoinHistoryResponse;
import com.jocoos.mybeautip.domain.event.dto.EventJoinResponse;
import com.jocoos.mybeautip.domain.event.service.EventJoinService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.restapi.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EventJoinController {

    private final LegacyMemberService legacyMemberService;
    private final EventJoinService service;

    @PostMapping("/1/event/join/{eventId}")
    public EventJoinResponse joinEvent(@PathVariable long eventId) {
        long memberId = legacyMemberService.currentMemberId();
        service.join(eventId, memberId);

        return EventJoinResponse.builder()
                .result(2)
                .build();
    }

    @GetMapping("/1/event/join/history")
    public CursorResponse getJoinEventList(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) Long cursor,
                                           @RequestParam(defaultValue = "20") int size) {

        long memberId = legacyMemberService.currentMemberId();
        List<EventJoinHistoryResponse> content = service.selectEventJoinHistory(memberId, cursor, size);

        String nextCursor = String.valueOf(Long.MAX_VALUE);
        if (CollectionUtils.isNotEmpty(content)) {
            nextCursor = String.valueOf(content.get(content.size()-1).getId());
        }

        return new CursorResponse.Builder<>("/api/1/event/join/history", content)
                .withCount(size)
                .withCursor(nextCursor)
                .toBuild();
    }
}
