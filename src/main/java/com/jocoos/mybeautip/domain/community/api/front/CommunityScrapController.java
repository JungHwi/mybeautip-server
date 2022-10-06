package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.converter.ScrapConverter;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.service.ScrapService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
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
public class CommunityScrapController {

    private final ScrapService service;
    private final ScrapConverter converter;

    @PatchMapping("/1/community/{community_id}/scrap")
    public ResponseEntity<CommunityScrapResponse> scrap(@PathVariable(name = "community_id") long communityId,
                                                        @RequestBody BooleanDto isScrap) {
        ScrapRequest request = ScrapRequest.builder()
                .type(ScrapType.COMMUNITY)
                .relationId(communityId)
                .isScrap(isScrap.isBool())
                .build();

        CommunityScrapResponse response = service.scrap(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/1/community/scrap")
    public ResponseEntity<CursorResultResponse<CommunityScrapResponse>> getScraps(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                                  @RequestParam(required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<CommunityScrapResponse> response = service.getScrapList(ScrapType.COMMUNITY, cursor, pageable);
        CursorResultResponse<CommunityScrapResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }
}
