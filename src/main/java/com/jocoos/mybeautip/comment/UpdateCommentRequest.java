package com.jocoos.mybeautip.comment;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;
}
