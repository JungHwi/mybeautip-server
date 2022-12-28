package com.jocoos.mybeautip.domain.notice.api.admin;

import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse;
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest;
import com.jocoos.mybeautip.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminNoticeController {

    private final NoticeService service;

    @PostMapping("/1/notice")
    public ResponseEntity<NoticeResponse> write(@RequestBody WriteNoticeRequest request) {
        NoticeResponse response = service.write(request);
        return ResponseEntity.ok(response);
    }
}
