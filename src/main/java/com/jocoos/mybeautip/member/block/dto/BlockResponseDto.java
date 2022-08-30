package com.jocoos.mybeautip.member.block.dto;

import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlockResponseDto {
    private final Long memberId;
    private final Long targetId;
    private final Boolean blocked;

    public static BlockResponseDto from(Block block) {
       return new BlockResponseDto(block.getMe(), block.getYouId(), BlockStatus.BLOCK.equals(block.getStatus()));
    }
}
