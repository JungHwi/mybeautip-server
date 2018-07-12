package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsListRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class Response {
  private Integer code;
  private String message;
  
  private List<Goods> goods;
  private String nextCursor;
  private String nextRef;
  
  public String generateNextRef(GoodsListRequest request, String nextCursor) {
    StringBuilder nextRef = new StringBuilder();
    nextRef.append("/api/1/goods?cursor=").append(nextCursor)
        .append("&count=").append(request.getCount())
        .append("&category=").append(request.getCategory())
        .append("&keyword=").append(request.getKeyword());
    
    return nextRef.toString();
  }
}