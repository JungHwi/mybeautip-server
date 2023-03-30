package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.domain.member.service.MemberActivityService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyActivityController {

    private final MemberActivityService service;

    @GetMapping(value = "/1/my/activity")
    public <T extends CursorInterface> CursorResultResponse<T> getMyActivities(
            @RequestParam(required = false, defaultValue = "COMMUNITY") MemberActivityType type,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "20") int size,
            @CurrentMember MyBeautipUserDetails userDetails) {
        List<T> response = service.get(type, cursor, size, userDetails.getMember());
        return new CursorResultResponse<>(response);
    }
}
