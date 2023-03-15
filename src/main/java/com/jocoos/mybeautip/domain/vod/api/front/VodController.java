package com.jocoos.mybeautip.domain.vod.api.front;

import com.jocoos.mybeautip.domain.vod.code.VodSortField;
import com.jocoos.mybeautip.domain.broadcast.dto.HeartCountResponse;
import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.service.VodService;
import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.ReportCountResponse;
import com.jocoos.mybeautip.global.dto.single.IntegerDto;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class VodController {

    private final VodService service;

    @GetMapping("/1/vod")
    public CursorResultResponse<VodResponse> getList(@RequestParam(required = false, defaultValue = "1") Long categoryId,
                                                     @RequestParam(required = false) Long cursor,
                                                     @RequestParam(required = false, defaultValue = "CREATED_AT") VodSortField sort,
                                                     @RequestParam(required = false, defaultValue = "DESC") String order,
                                                     @RequestParam(required = false, defaultValue = "5") int size) {
        CursorPaging<Long> cursorPaging = CursorPaging.idCursorWithNonUniqueSortField(cursor, sort.getSortField(), order, size);
        List<VodResponse> responses = service.getList(categoryId, cursorPaging);
        return new CursorResultResponse<>(responses);
    }

    @GetMapping("/1/vod/{vodId}")
    public VodResponse get(@PathVariable long vodId) {
        return service.get(vodId);
    }

    @PostMapping("/1/vod/{vodId}/report")
    public ReportCountResponse report(@PathVariable long vodId,
                                      @CurrentMember MyBeautipUserDetails userDetails,
                                      @RequestBody ReportRequest request) {
        return service.report(vodId, userDetails.getMember().getId(), request.getDescription());
    }

    @PostMapping("/1/vod/{vodId}/heart")
    public HeartCountResponse addHeartCount(@PathVariable long vodId, @RequestBody IntegerDto count) {
        return service.addHeartCount(vodId, count.getNumber());
    }
}
