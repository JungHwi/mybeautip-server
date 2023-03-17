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
}
