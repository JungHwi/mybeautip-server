package com.jocoos.mybeautip.domain.notice.api.front;

import com.jocoos.mybeautip.domain.notice.dto.NoticeListResponse;
import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse;
import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.service.NoticeService;
import com.jocoos.mybeautip.domain.popupnotice.code.NoticeStatus;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService service;

    @GetMapping("/1/notice")
    public ResponseEntity<CursorResultResponse<NoticeListResponse>> list(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) Long cursor,
                                                                                  @RequestParam(required = false, defaultValue = "20") int size) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "isImportant", Sort.NullHandling.NULLS_LAST));
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));

        Pageable pageable = PageRequest.of(0, size, Sort.by(orders));
        SearchNoticeRequest request = SearchNoticeRequest.builder()
                .status(NoticeStatus.ACTIVE)
                .cursor(cursor)
                .pageable(pageable)
                .build();

        Page<NoticeListResponse> response = service.search(request);
        CursorResultResponse<NoticeListResponse> result = new CursorResultResponse<>(response.getContent());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/1/notice/{noticeId}")
    public ResponseEntity<NoticeResponse> get(@PathVariable long noticeId) {
        NoticeResponse response = service.get(noticeId, true);
        return ResponseEntity.ok(response);
    }
}
