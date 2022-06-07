package com.jocoos.mybeautip.comment;

import com.jocoos.mybeautip.member.mention.MentionTag;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;

    private Long parentId;

    private List<MentionTag> mentionTags;
}
