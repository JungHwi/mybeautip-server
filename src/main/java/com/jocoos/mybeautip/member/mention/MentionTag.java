package com.jocoos.mybeautip.member.mention;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
public class MentionTag {
  private String username;
  private Long memberId;

  public MentionTag(Member member) {
    this.username = member.getUsername();
    this.memberId = member.getId();
  }
}
