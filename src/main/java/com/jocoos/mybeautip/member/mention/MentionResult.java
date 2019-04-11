package com.jocoos.mybeautip.member.mention;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MentionResult {
  private String comment;
  private Set<MentionTag> mentionInfo;

  public void add(MentionTag mentionTag) {
    if (mentionInfo == null) {
      mentionInfo = new HashSet<>();
    }

    this.mentionInfo.add(mentionTag);
  }
}
