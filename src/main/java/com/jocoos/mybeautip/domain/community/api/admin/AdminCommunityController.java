package com.jocoos.mybeautip.domain.community.api.admin;

import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.service.AdminCommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminCommunityController {

    private final AdminCommunityService service;

    @GetMapping("/community/category")
    public ResponseEntity<List<CommunityCategoryResponse>> getCommunityCategories() {
        return ResponseEntity.ok(service.getCategories());
    }

//    @GetMapping("/community")
//    public ResponseEntity<PageResponse<List<AdminCommunityResponse>>> getCommunities(
//            @RequestParam(required = false) Long categoryId,
//            @RequestParam(required = false, defaultValue = "1") Long page,
//            @RequestParam(required = false, defaultValue = "10") Long size,
//            @RequestParam(required = false, defaultValue = "createdAt") String sort,
//            @RequestParam(required = false, defaultValue = "DESC") String order,
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt) {
//
//        CommunitySearchCondition condition = CommunitySearchCondition.builder()
//                .paging(Paging.page(page, size))
//                .sort(new Sort(sort, order))
//                .searchKeyword(SearchKeyword.from(search, startAt, endAt, ZoneId.of("Asia/Seoul")))
//                .build();
//
//        return ResponseEntity.ok(service.getCommunities(categoryId, condition));
//    }
}
