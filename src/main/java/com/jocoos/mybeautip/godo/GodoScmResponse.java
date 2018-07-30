package com.jocoos.mybeautip.godo;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "data")
public class GodoScmResponse {
  private GodoScmResponse.Header header;
  private List<GodoScmResponse.CodeData> body;

  @XmlElement(name = "header")
  public void setHeader(GodoScmResponse.Header header) {
    this.header = header;
  }

  @XmlElementWrapper
  @XmlElement(name = "code_data")
  public void setReturn(List<GodoScmResponse.CodeData> body) {
    this.body = body;
  }

  @Data
  public static class Header {
    private String code;
    private String msg;
  }

  @Data
  public static class CodeData {
    private String scmNo;
    private String companyNm;
  }
}