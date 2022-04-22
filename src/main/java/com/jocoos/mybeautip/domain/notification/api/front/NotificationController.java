package com.jocoos.mybeautip.domain.notification.api.front;

import com.jocoos.mybeautip.domain.notification.converter.NotificationCenterConvert;
import com.jocoos.mybeautip.domain.notification.dto.CenterMessageResponse;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationCenterEntity;
import com.jocoos.mybeautip.domain.notification.service.CenterServiceImpl;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/2/notification")
public class NotificationController {

    private final CenterServiceImpl service;
    private final MemberService memberService;

    private final NotificationCenterConvert converter;

    @GetMapping
    public CursorResponse getCenterMessages(@RequestParam(required = false) Long cursor,
                                            @RequestParam(defaultValue = "20") int size) {

        Member member = memberService.currentMember();

        cursor = (cursor == null) ? Long.MAX_VALUE : cursor;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<NotificationCenterEntity> notificationCenters = service.getUserCenterMessage(member.getId(), cursor, pageable);
        List<CenterMessageResponse> result = converter.convert(notificationCenters.getContent());

        return new CursorResponse.Builder<>("/api/2/notification/", result)
                .withCount(size)
                .withCursor(String.valueOf(result.get(result.size()-1).getId()))
                .toBuild();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Integer> patchRead(@PathVariable Long id) {
        Member member = memberService.currentMember();
        return new ResponseEntity<>(service.patchRead(member.getId(), id), HttpStatus.OK);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Integer> patchRead() {
        Member member = memberService.currentMember();
        return new ResponseEntity<>(service.patchReadAll(member.getId()), HttpStatus.OK);
    }

}
