package com.jocoos.mybeautip.restapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jocoos.mybeautip.goods.Goods;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Response {
  private Integer code;
  private String message;
  
  private List<Goods> goods;
  private String nextCursor;
}