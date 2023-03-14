package com.jocoos.mybeautip.domain.member.api.internal;

import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javax.validation.Valid;

import com.jocoos.mybeautip.domain.community.dto.MemberResponse;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.dto.*;
import com.jocoos.mybeautip.domain.member.service.AdminMemberService;
import com.jocoos.mybeautip.domain.member.service.InfluencerService;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchCondition;
import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/internal")
@RestController
public class InternalMemberController {

    private final AdminMemberService service;
    private final InfluencerService influencerService;

    @GetMapping("/1/member/status")
    public ResponseEntity<List<MemberStatusResponse>> getStatusesWithCount() {
        return ResponseEntity.ok(service.getStatusesWithCount());
    }

    @GetMapping("/1/member")
    public ResponseEntity<PageResponse<AdminMemberResponse>> getMembers(
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(name = "is_influencer", required = false) Boolean isInfluencer,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(name = "grant_type", required = false) GrantType grantType,
            @RequestParam(required = false) String search,
            @RequestParam(name = "start_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(name = "end_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
            @RequestParam(required = false, name = "is_reported") Boolean isReported) {

        SearchOption searchOption = SearchOption.builder()
                .searchQueryString(search)
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .isReported(isReported)
                .isInfluencer(isInfluencer)
                .build();

        MemberSearchCondition condition = MemberSearchCondition.builder()
                .grantType(grantType)
                .status(status)
                .pageable(PageRequest.of(page - 1, size))
                .searchOption(searchOption)
                .build();

        return ResponseEntity.ok(service.getMembers(condition));
    }

    @GetMapping("/1/member/{memberId}")
    public ResponseEntity<AdminMemberDetailResponse> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(service.getMember(memberId));
    }

    @GetMapping("/1/member/{memberId}/point")
    public ResponseEntity<PageResponse<AdminMemberPointResponse>> getMemberPointHistory(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getPointHistory(memberId, PageRequest.of(page - 1, size)));
    }

    @GetMapping("/1/member/{memberId}/report")
    public ResponseEntity<PageResponse<AdminMemberReportResponse>> getMemberReportHistory(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getReportHistory(memberId, PageRequest.of(page - 1, size)));
    }

    @PatchMapping("/1/member/username/refresh")
    public ResponseEntity refreshUsername() {
        service.refreshUsername();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/1/member/{memberId}/status")
    public ResponseEntity changeStatus(@PathVariable Long memberId,
                                       @RequestBody MemberStatusRequest request) {
        long adminId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        request.setAdminId(adminId);
        request.setMemberId(memberId);
        service.updateStatus(request);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/1/member/influencer")
    public ResponseEntity<List<InfluencerResponse>> bulkUpdateInfluencer(@RequestBody @Valid InfluencerRequest request) {
        return ResponseEntity.ok(influencerService.bulkUpdateInfluencer(request));
    }

    @PostMapping("/1/member")
    public ResponseEntity saveMember(@RequestBody MemberRegistrationRequest request) {

        log.debug("{}", request);
        MemberResponse response = service.saveOrUpdate(request);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
