package com.jocoos.mybeautip.domain.community.api.admin;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.service.AdminCommunityService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
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

    @GetMapping("/community")
    public ResponseEntity<PageResponse<AdminCommunityResponse>> getCommunities(
            @RequestParam(required = false, name = "category_id") Long categoryId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "sortedAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String order,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, name = "start_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(required = false, name = "end_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.fromString(order), sort);
        SearchOption searchOption = SearchOption.from(search, startAt, endAt, ZoneId.of("Asia/Seoul"));

        return ResponseEntity.ok(service.getCommunities(categoryId, pageRequest, searchOption));
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<AdminCommunityResponse> getCommunity(@PathVariable Long communityId) {
        return ResponseEntity.ok(service.getCommunity(communityId));
    }

    @PatchMapping("/community/{communityId}/winning")
    public ResponseEntity<IdDto> winCommunity(@PathVariable Long communityId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.winCommunity(communityId, request.isBool())));
    }

    @PatchMapping("/community/{communityId}/fix")
    public ResponseEntity<IdDto> fixCommunity(@PathVariable Long communityId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.fixCommunity(communityId, request.isBool())));
    }

    @PatchMapping("/community/{communityId}/hiding")
    public ResponseEntity<IdDto> hideCommunity(@PathVariable Long communityId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.hideCommunity(communityId, request.isBool())));
    }
}
