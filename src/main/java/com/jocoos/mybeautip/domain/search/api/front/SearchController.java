package com.jocoos.mybeautip.domain.search.api.front;

import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.CountResponse;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.service.SearchService;
import com.jocoos.mybeautip.domain.search.valid.KeywordConstraint;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/1")
@RestController
public class SearchController {

    private final LegacyMemberService legacyMemberService;
    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<SearchResponse<?>> search(
            @RequestParam(required = false, defaultValue = "COMMUNITY") SearchType type,
            @RequestParam @KeywordConstraint String keyword,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {

        Member member = legacyMemberService.currentMember();
        KeywordSearchCondition condition = new KeywordSearchCondition(keyword, cursor, size);

        return ResponseEntity.ok(searchService.search(type, condition, member));
    }

    @GetMapping("/search/count")
    public ResponseEntity<CountResponse> searchCount(
            @RequestParam(required = false, defaultValue = "COMMUNITY") SearchType type,
            @RequestParam @KeywordConstraint String keyword) {
        return ResponseEntity.ok(searchService.count(type, keyword));
    }
}