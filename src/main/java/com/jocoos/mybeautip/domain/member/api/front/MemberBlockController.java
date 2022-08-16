package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.member.dto.MemberBlockRequest;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockService;
import com.jocoos.mybeautip.member.block.dto.BlockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class MemberBlockController {

    private final BlockService blockService;
    private final LegacyMemberService legacyMemberService;

    @PatchMapping("/1/member/block")
    public ResponseEntity<BlockResponseDto> block(@Valid @RequestBody MemberBlockRequest request) {
        Block block = blockService.changeTargetBlockStatus(
                legacyMemberService.currentMemberId(),
                request.getTargetId(),
                request.getIsBlock());
        return ResponseEntity.ok(BlockResponseDto.from(block));
    }
}
