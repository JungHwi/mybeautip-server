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
import com.jocoos.mybeautip.domain.member.service.MemberSignupService;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchCondition;
import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockService;
import com.jocoos.mybeautip.member.block.dto.BlockResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/internal")
@RestController
public class InternalMemberController {

    private final AdminMemberService service;
    private final BlockService blockService;
    private final LegacyMemberService legacyMemberService;
    private final MemberSignupService memberSignupService;

    @GetMapping("/member")
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

    @PostMapping("/1/member")
    public ResponseEntity saveMember(@RequestBody MemberRegistrationRequest request) {

        log.debug("{}", request);
        MemberResponse response = service.saveOrUpdate(request);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PatchMapping("/1/member/block")
    public ResponseEntity<BlockResponseDto> block(@Valid @RequestBody MemberBlockRequest request) {
        Block block = blockService.changeTargetBlockStatus(
            legacyMemberService.currentMemberId(),
            request.getTargetId(),
            request.getIsBlock());
        return ResponseEntity.ok(BlockResponseDto.from(block));
    }

    @PatchMapping("/1/member/withdrawal")
    public ResponseEntity withdrawal(@RequestBody String reason) {
        memberSignupService.withdrawal(reason);

        return new ResponseEntity(HttpStatus.OK);
    }
}
