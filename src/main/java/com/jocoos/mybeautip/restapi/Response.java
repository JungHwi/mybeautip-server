package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsListRequest;
import lombok.Data;

import java.util.List;

@Data
public class Response<T> {
  private List<Goods> goods;
  private List<T> content;
  
  private String nextCursor;
  private String nextRef;
  
  private Boolean follow;
  
  public String generateNextRef(GoodsListRequest request, String nextCursor) {
    StringBuilder nextRef = new StringBuilder();
    nextRef.append("/api/1/goods?cursor=").append(nextCursor)
        .append("&count=").append(request.getCount())
        .append("&category=").append(request.getCategory())
        .append("&keyword=").append(request.getKeyword());
    
    return nextRef.toString();
  }
  
  public String generateNextRef(String requestUri, String nextCursor, int count) {
    StringBuilder nextRef = new StringBuilder();
    nextRef.append(requestUri)
        .append("?cursor=").append(nextCursor)
        .append("&count=").append(count);
    
    return nextRef.toString();
  }
}