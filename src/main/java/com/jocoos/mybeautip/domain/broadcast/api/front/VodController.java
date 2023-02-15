package com.jocoos.mybeautip.domain.broadcast.api.front;

import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.service.VodService;
import com.jocoos.mybeautip.domain.event.code.SortField;
import com.jocoos.mybeautip.global.dto.IsVisibleResponse;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class VodController {

    private final VodService service;

    @GetMapping("/1/vod")
    public CursorResultResponse<VodResponse> getVodList(@RequestParam(required = false, defaultValue = "1") Long categoryId,
                                                     @RequestParam(required = false) Long cursor,
                                                     @RequestParam(required = false, defaultValue = "CREATED_AT") SortField sort,
                                                     @RequestParam(required = false, defaultValue = "DESC") String order,
                                                     @RequestParam(required = false, defaultValue = "20") int size) {
        CursorPaging<Long> cursorPaging = CursorPaging.idCursorWithNonUniqueSortField(cursor, sort, order, size);
        List<VodResponse> responses = service.getVodList(categoryId, cursorPaging);
        return new CursorResultResponse<>(responses);
    }

    @PatchMapping("/1/vod/{vodId}/visibility")
    public IsVisibleResponse changeVodVisibility(@PathVariable Long vodId, @RequestBody BooleanDto isVisible) {
        return service.changeVodVisibility(vodId, isVisible.isBool());
    }
}
