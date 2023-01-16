package com.jocoos.mybeautip.comment;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import com.jocoos.mybeautip.member.mention.MentionTag;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class CreateCommentRequest {

    @Size(max = 500)
    private String comment;

    private FileDto file;

    private Long parentId;

    private List<MentionTag> mentionTags;

    public String getFilename() {
        return file == null ? null : FileUtil.getFileName(file.getUrl());
    }
}
