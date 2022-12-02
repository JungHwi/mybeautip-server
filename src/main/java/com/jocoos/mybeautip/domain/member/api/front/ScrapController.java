package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.service.ScrapService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService service;

    @GetMapping("/1/my/scrap")
    public ResponseEntity<CursorResultResponse<CommunityScrapResponse>> getScraps(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                                  @RequestParam(required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<CommunityScrapResponse> response = service.getScrapList(ScrapType.COMMUNITY, cursor, pageable);
        CursorResultResponse<CommunityScrapResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/1/my/scrap/exist")
    public ResponseEntity<BooleanDto> isScrapExist() {
        return ResponseEntity.ok(new BooleanDto(service.isScrapExist()));
    }
}
