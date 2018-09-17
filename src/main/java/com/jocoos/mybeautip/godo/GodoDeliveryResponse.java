package com.jocoos.mybeautip.godo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

@Data
@XmlRootElement(name = "data")
public class GodoDeliveryResponse {
  private GodoDeliveryResponse.Header header;
  private List<GodoDeliveryResponse.CodeData> body;

  @XmlElement(name = "header")
  public void setHeader(GodoDeliveryResponse.Header header) {
    this.header = header;
  }

  @XmlElementWrapper
  @XmlElement(name = "code_data")
  public void setReturn(List<GodoDeliveryResponse.CodeData> body) {
    this.body = body;
  }

  @Data
  public static class Header {
    private String code;
    private String msg;
  }

  @Data
  public static class CodeData {
    private Integer sno;
    private Integer scmNo;
    private String method;
    private String description;
    private String collectFl;
    private String fixFl;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ChargeData> chargeData;
  }

  @Data
  public static class ChargeData {
    private BigDecimal unitStart;
    private BigDecimal unitEnd;
    private BigDecimal price;
  }
}