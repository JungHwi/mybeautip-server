package com.jocoos.mybeautip.domain.notice.api.front;

import com.jocoos.mybeautip.domain.notice.dto.PopupNoticeResponse;
import com.jocoos.mybeautip.domain.notice.service.PopupNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class PopupNoticeController {

    private final PopupNoticeService popupNoticeService;

    @GetMapping("/1/popup/notice")
    public ResponseEntity<List<PopupNoticeResponse>> getPopupNotices() {
        return ResponseEntity.ok(popupNoticeService.getNotices());
    }
}
