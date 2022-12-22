package com.jocoos.mybeautip.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PatchVideoCommentRequest {

    @NotBlank
    private String contents;

}
