package com.jocoos.mybeautip.domain.notice.api.admin;

import com.jocoos.mybeautip.domain.notice.code.NoticeSort;
import com.jocoos.mybeautip.domain.notice.dto.*;
import com.jocoos.mybeautip.domain.notice.service.NoticeService;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.jocoos.mybeautip.domain.notice.code.NoticeStatus.NORMAL;

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
    public ResponseEntity<PageResponse<NoticeListResponse>> search(@RequestParam(required = false, defaultValue = "1") int page,
                                                                   @RequestParam(required = false, defaultValue = "10") int size,
                                                                   @RequestParam(required = false, defaultValue = "id") String sort,
                                                                   @RequestParam(required = false, defaultValue = "DESC") String order,
                                                                   @RequestParam(required = false) String search,
                                                                   @RequestParam(required = false, name = "start_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
                                                                   @RequestParam(required = false, name = "end_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt) {

        PageRequest pageRequest = PageRequest.of(
                page - 1, size,
                Sort.Direction.fromString(order),
                NoticeSort.getColumn(sort));

        SearchOption searchOption = SearchOption.builder()
                .searchQueryString(search)
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .build();

        SearchNoticeRequest request = SearchNoticeRequest.builder()
                .search(searchOption)
                .status(NORMAL)
                .pageable(pageRequest)
                .build();

        Page<NoticeListResponse> result = service.search(request);

        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeResponse> get(@PathVariable long noticeId) {
        NoticeResponse result = service.get(noticeId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeResponse> edit(@PathVariable long noticeId,
                                               @RequestBody EditNoticeRequest request) {

        request.setId(noticeId);
        NoticeResponse response = service.edit(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity delete(@PathVariable long noticeId) {
        service.delete(noticeId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
