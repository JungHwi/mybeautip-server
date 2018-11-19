package com.jocoos.mybeautip.goods;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Data
public class GoodsListRequest {
  @Size(max = 13)
  private String cursor = "";
  
  @Max(100)
  private int count = 20;
  
  @Size(max=6)
  private String category = "";
  
  @Size(max=100)
  private String keyword = "";
}