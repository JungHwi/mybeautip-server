package com.jocoos.mybeautip.support.slack;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SlackMessageFormat {
  private String title;
  private String message;

  @Override
  public String toString() {
    return String.format("*%s*" +
        "```%s```", title, message);
  }
}
