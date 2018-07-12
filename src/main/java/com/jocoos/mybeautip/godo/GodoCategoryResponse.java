package com.jocoos.mybeautip.godo;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "data")
public class GodoCategoryResponse {
  private Header header;
  private List<CategoryData> body;
  
  @XmlElement(name = "header")
  public void setHeader(Header header) {
    this.header = header;
  }
  
  @XmlElementWrapper
  @XmlElement(name = "category_data")
  public void setReturn(List<CategoryData> body) {
    this.body = body;
  }
  
  @Data
  public static class Header {
    private String code;
    private String msg;
  }
  
  @Data
  public static class CategoryData {
    private String cateCd;
    private String cateNm;
    private String cateDisplayFl;
    private String cateDisplayMobileFl;
  }
}