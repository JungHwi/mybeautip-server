package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.service.ScrapService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
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
public class MyScrapController {

    private final ScrapService service;

    /**
     * @deprecated
     */
    @Deprecated(since = "클라이언트 비디오 업로드 완성 후", forRemoval = true)
    @GetMapping("/1/my/scrap")
    public ResponseEntity<CursorResultResponse<MyScrapResponse<CommunityResponse>>> getScrapsDeprecated(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                                            @RequestParam(required = false, defaultValue = "20") int size,
                                                                                            @CurrentMember MyBeautipUserDetails userDetails) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<MyScrapResponse<CommunityResponse>> response = service.getScrapList(ScrapType.COMMUNITY, cursor, pageable, userDetails.getMember().getId());

        for (MyScrapResponse<CommunityResponse> communityScrapResponse : response) {
            communityScrapResponse.getResponse().toV1();
        }

        return ResponseEntity.ok(new CursorResultResponse<>(response));
    }

    @GetMapping("/2/my/scrap")
    public <T extends CursorInterface> CursorResultResponse<MyScrapResponse<T>> getScraps(@RequestParam(required = false, defaultValue = "COMMUNITY") ScrapType type,
                                                                                          @RequestParam(required = false, defaultValue = MAX_LONG_STRING) Long cursor,
                                                                                          @RequestParam(required = false, defaultValue = "20") int size,
                                                                                          @CurrentMember MyBeautipUserDetails userDetails) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<MyScrapResponse<T>> response = service.getScrapList(type, cursor, pageable, userDetails.getMember().getId());
        return new CursorResultResponse<>(response);
    }

    @GetMapping("/1/my/scrap/exist")
    public ResponseEntity<BooleanDto> isScrapExist() {
        return ResponseEntity.ok(new BooleanDto(service.isScrapExist()));
    }
}
