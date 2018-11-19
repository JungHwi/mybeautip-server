package com.jocoos.mybeautip.post;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class PostContent implements Serializable {

  @NotNull
  private int seq;

  @NotNull
  private int category;

  @NotNull
  @Size(max = 1000)
  private String content;
}
