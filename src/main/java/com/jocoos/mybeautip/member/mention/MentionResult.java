package com.jocoos.mybeautip.member.mention;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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
