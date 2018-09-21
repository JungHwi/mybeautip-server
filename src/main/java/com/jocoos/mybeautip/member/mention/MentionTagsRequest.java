package com.jocoos.mybeautip.member.mention;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class MentionTagsRequest extends ArrayList<MentionTagsRequest.MentionTag> {

  public MentionTagsRequest(List<MentionTag> mentionTags) {
    super(mentionTags);
  }

  @Data
  public class MentionTag {
    private String username;
    private Long memberId;
  }
}
