package com.jocoos.mybeautip.member.block;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.MemberInfo;

@Data
@NoArgsConstructor
public class BlockInfo {
  private Long blockId;
  private Long createdAt;
  private MemberInfo member;
  
  public BlockInfo(Block block) {
    this.blockId = block.getId();
    this.createdAt = block.getCreatedAt().getTime();
  }
}