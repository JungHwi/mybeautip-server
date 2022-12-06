package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.domain.event.service.AdminEventSortOrderService;
import com.jocoos.mybeautip.global.dto.SortOrderDto;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminEventSortOrderController {

    private final AdminEventSortOrderService service;

    @PatchMapping("/event/{eventId}/fix")
    public ResponseEntity<IdDto> topFix(@PathVariable Long eventId, @RequestBody BooleanDto isTopFix) {
        return ResponseEntity.ok(new IdDto(service.topFix(eventId, isTopFix.isBool())));
    }

    @PatchMapping("/event/order")
    public ResponseEntity<SortOrderDto> changeSortOrder(@RequestBody SortOrderDto request) {
        return ResponseEntity.ok(new SortOrderDto(service.changeSortOrder(request.getSortedIds())));
    }
}
