package com.jocoos.mybeautip.member.mention;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MentionTag {
  private String username;
  private Long memberId;
}
