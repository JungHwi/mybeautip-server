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

  public PostContent(int seq, ContentCategory category, String content) {
    this.seq = seq;
    this.category = category.value();
    this.content = content;
  }

  public enum ContentCategory {
    TEXT(1),
    IMAGE(2),
    VIDEO(4);

    int value;

    ContentCategory(int value) {
      this.value = value;
    }

    public int value() {
      return value;
    }
  }
}
