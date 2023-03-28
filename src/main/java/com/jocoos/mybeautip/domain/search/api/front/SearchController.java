package com.jocoos.mybeautip.domain.search.api.front;

import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.CountResponse;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.service.SearchService;
import com.jocoos.mybeautip.domain.search.valid.KeywordConstraint;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
@RestController
public class SearchController {

    private final SearchService searchService;

    /**
     * @deprecated
     */
    @Deprecated(since = "클라이언트 비디오 업로드 완성 후", forRemoval = true)
    @GetMapping("/1/search")
    public SearchResponse<?> searchDeprecated(
            @RequestParam(required = false, defaultValue = "COMMUNITY") SearchType type,
            @RequestParam @KeywordConstraint String keyword,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") @Min(1) int size,
            @CurrentMember MyBeautipUserDetails userDetails) {

        KeywordSearchRequest condition = KeywordSearchRequest.builder()
                .member(userDetails.getMember())
                .keyword(keyword)
                .size(size)
                .cursor(cursor)
                .build();

        SearchResponse<?> response = searchService.search(type, condition);
        return response.toV1();
    }

    @GetMapping("/2/search")
    public <T extends CursorInterface> SearchResponse<T> search(
            @RequestParam(required = false, defaultValue = "COMMUNITY") SearchType type,
            @RequestParam @KeywordConstraint String keyword,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") @Min(1) int size,
            @CurrentMember MyBeautipUserDetails userDetails) {

        KeywordSearchRequest condition = KeywordSearchRequest.builder()
                .member(userDetails.getMember())
                .tokenUsername(userDetails.getUsername())
                .keyword(keyword)
                .size(size)
                .cursor(cursor)
                .build();

        return searchService.search(type, condition);
    }

    @GetMapping("/1/search/count")
    public CountResponse count(
            @RequestParam(required = false, defaultValue = "COMMUNITY") SearchType type,
            @RequestParam @KeywordConstraint String keyword,
            @CurrentMember MyBeautipUserDetails userDetails) {
        return searchService.count(type, keyword, userDetails.getMember());
    }
}
