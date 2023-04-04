package com.jocoos.mybeautip.domain.vod.api.front;

import com.jocoos.mybeautip.domain.broadcast.dto.HeartCountResponse;
import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponseV2;
import com.jocoos.mybeautip.domain.vod.code.VodSortField;
import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.service.VodService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.ReportCountResponse;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IntegerDto;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
                                                     @RequestParam(required = false, defaultValue = "DESC") Direction order,
                                                     @RequestParam(required = false, defaultValue = "5") int size,
                                                     @CurrentMember MyBeautipUserDetails userDetails) {
        CursorPaging<Long> cursorPaging = CursorPaging.idCursorWithNonUniqueSortField(cursor, sort.getSortField());
        Pageable pageable = PageRequest.of(0, size, sort.getSort(order));
        List<VodResponse> responses = service.getList(categoryId, cursorPaging, pageable, userDetails.getUsername());
        return new CursorResultResponse<>(responses);
    }

    @GetMapping("/1/vod/{vodId}")
    public VodResponse get(@PathVariable long vodId,
                           @CurrentMember MyBeautipUserDetails userDetails) {
        return service.get(vodId, userDetails.getUsername());
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

    @PatchMapping("/1/vod/{vodId}/scrap")
    public ScrapResponseV2 scrap(@PathVariable Long vodId,
                                 @RequestBody BooleanDto isScrap,
                                 @CurrentMember MyBeautipUserDetails userDetails) {
        return service.scrap(vodId, userDetails.getMember().getId(), isScrap.isBool());
    }
}
