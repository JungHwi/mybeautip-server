package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.goods.Goods;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class Response {
  private Integer code;
  private String message;
  
  private List<Goods> goods;
  private String nextCursor;
}