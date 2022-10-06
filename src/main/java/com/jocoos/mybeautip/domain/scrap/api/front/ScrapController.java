package com.jocoos.mybeautip.domain.scrap.api.front;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.service.ScrapService;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScrapController {

    private final ScrapService service;

    @PostMapping("/1/community/scrap")
    public ResponseEntity<ScrapResponse> scrap(@RequestBody ScrapRequest request) {
        ScrapResponse response = service.scrap(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/1/community/scrap")
    public ResponseEntity<CursorResultResponse<ScrapResponse>> getScrap(@RequestParam(required = false, defaultValue = "VIDEO")ScrapType type,
                                                                        @RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                        @RequestParam(required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<ScrapResponse> response = service.getScrapList(type, cursor, pageable);
        CursorResultResponse<ScrapResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }
}
