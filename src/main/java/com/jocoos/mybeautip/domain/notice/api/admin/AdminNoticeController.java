package com.jocoos.mybeautip.domain.notice.api.admin;

import com.jocoos.mybeautip.domain.notice.code.NoticeSort;
import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse;
import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest;
import com.jocoos.mybeautip.domain.notice.service.NoticeService;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminNoticeController {

    private final NoticeService service;

    @PostMapping("/notice")
    public ResponseEntity<NoticeResponse> write(@RequestBody WriteNoticeRequest request) {
        NoticeResponse response = service.write(request);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @GetMapping("/notice")
    public ResponseEntity<PageResponse<NoticeResponse>> search(@RequestParam(required = false, defaultValue = "1") int page,
                                                               @RequestParam(required = false, defaultValue = "10") int size,
                                                               @RequestParam(required = false, defaultValue = "ID") NoticeSort sort,
                                                               @RequestParam(required = false, defaultValue = "DESC") String order,
                                                               @RequestParam(required = false) String search,
                                                               @RequestParam(required = false, name = "start_at") @DateTimeFormat(pattern = "yyyy-MM-dd") ZonedDateTime startAt,
                                                               @RequestParam(required = false, name = "end_at") @DateTimeFormat(pattern = "yyyy-MM-dd") ZonedDateTime endAt) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.Direction.fromString(order), sort.getColumn());

        SearchNoticeRequest request = SearchNoticeRequest.builder()
                .search(search)
                .startAt(startAt)
                .endAt(endAt)
                .pageable(pageRequest)
                .build();

        Page<NoticeResponse> result = service.search(request);

        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeResponse> get(@PathVariable long noticeId) {
        NoticeResponse result = service.get(noticeId);
        return ResponseEntity.ok(result);
    }
}
