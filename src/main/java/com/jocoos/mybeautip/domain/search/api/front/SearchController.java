package com.jocoos.mybeautip.domain.search.api.front;

import com.jocoos.mybeautip.domain.search.service.SearchService;
import com.jocoos.mybeautip.domain.search.valid.KeywordConstraint;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
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
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/1")
@RestController
public class SearchController {

    private final LegacyMemberService legacyMemberService;
    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<CursorResultResponse<?>> search(
            @RequestParam @KeywordConstraint String keyword,
            @RequestParam(required = false) ZonedDateTime cursor,
            @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {

        Member member = legacyMemberService.currentMember();
        KeywordSearchCondition condition = new KeywordSearchCondition(keyword, cursor, size);

        return ResponseEntity.ok(new CursorResultResponse<>(searchService.searchCommunity(condition, member)));
    }
}
