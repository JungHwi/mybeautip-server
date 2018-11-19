package com.jocoos.mybeautip.member.mention;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MentionResult {
  private String comment;
  private List<MentionTag> mentionInfo;

  public void add(MentionTag mentionTag) {
    if (mentionInfo == null) {
      mentionInfo = Lists.newArrayList();
    }

    this.mentionInfo.add(mentionTag);
  }
}
