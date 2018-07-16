package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.member.MemberController;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlockInfo {
  private Long blockId;
  private Long createdAt;
  private MemberController.MemberInfo member;
  
  public BlockInfo(Block block) {
    this.blockId = block.getId();
    this.createdAt = block.getCreatedAt().getTime();
  }
}