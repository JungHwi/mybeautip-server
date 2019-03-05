package com.jocoos.mybeautip.comment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateCommentRequest {
  @NotNull
  @Size(max = 500)
  private String comment;
}
