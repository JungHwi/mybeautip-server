package com.jocoos.mybeautip.video;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class VideoCategory implements Serializable {

  @NotNull
  private int category;

}
