package com.jocoos.mybeautip.goods;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Data
public class GoodsListRequest {
  @Size(max = 120, message = "Invalid cursor")
  private String cursor = "";
  
  @Max(100)
  private Integer count = 20;
  
  private String category;
}