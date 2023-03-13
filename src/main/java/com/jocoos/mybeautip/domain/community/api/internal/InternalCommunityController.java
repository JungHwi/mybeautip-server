package com.jocoos.mybeautip.domain.community.api.internal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.PatchCommunityRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.service.AdminCommunityService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/internal/")
@RestController
public class InternalCommunityController {

    private final AdminCommunityService service;

    @PostMapping("/1/community")
    public ResponseEntity<AdminCommunityResponse> write(@CurrentMember MyBeautipUserDetails userDetails,
                                                        @RequestHeader("MEMBER-ID") String memberId,
                                                        @RequestBody WriteCommunityRequest request) {
        AdminCommunityResponse response = service.write(request, userDetails.getMember());
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/1/community/{communityId}")
    public ResponseEntity<IdDto> edit(@PathVariable Long communityId, @RequestBody PatchCommunityRequest request) {
        return ResponseEntity.ok(new IdDto(service.edit(communityId, request)));
    }

    @DeleteMapping("/1/community/{communityId}")
    public ResponseEntity<IdDto> deleteAdminWrite(@PathVariable Long communityId) {
        return ResponseEntity.ok(new IdDto(service.delete(communityId)));
    }

    @GetMapping("/1/community")
    public ResponseEntity<PageResponse<AdminCommunityResponse>> getCommunities(
            @RequestParam(required = false, name = "category_id") Long categoryId,
            @RequestParam(required = false, name = "event_id") Long eventId,
            @RequestParam(required = false) CommunityStatus status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "sortedAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String order,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, name = "start_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(required = false, name = "end_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
            @RequestParam(required = false, name = "is_reported") Boolean isReported,
            @CurrentMember MyBeautipUserDetails userDetails) {

        log.debug("{}", userDetails);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Direction.fromString(order), sort);
        SearchOption searchOption = SearchOption.builder()
                .searchQueryString(search)
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .isReported(isReported)
                .build();

        return ResponseEntity.ok(service.getCommunities(status, categoryId, eventId, pageRequest, searchOption));
    }

    @GetMapping("/1/community/{communityId}")
    public ResponseEntity<AdminCommunityResponse> getCommunity(@PathVariable Long communityId) {
        return ResponseEntity.ok(service.getCommunity(communityId));
    }

    @PatchMapping("/1/community/{communityId}/win")
    public ResponseEntity<IdDto> winCommunity(@PathVariable Long communityId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.winCommunity(communityId, request.isBool())));
    }

    @PatchMapping("/1/community/{communityId}/fix")
    public ResponseEntity<IdDto> fixCommunity(@PathVariable Long communityId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.fixCommunity(communityId, request.isBool())));
    }

    @PatchMapping("/1/community/{communityId}/hide")
    public ResponseEntity<IdDto> hideCommunity(@PathVariable Long communityId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.hideCommunity(communityId, request.isBool())));
    }
}
