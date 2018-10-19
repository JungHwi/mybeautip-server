package com.jocoos.mybeautip.godo;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class GodoGoodsResponse {
  private GodoGoodsResponse.Header header;
  private List<GoodsData> body;
  
  @XmlElement
  public void setHeader(GodoGoodsResponse.Header header) {
    this.header = header;
  }
  
  @XmlElementWrapper
  @XmlElement
  public void setReturn(List<GodoGoodsResponse.GoodsData> body) {
    this.body = body;
  }
  
  @Data
  public static class Header {
    private String code;
    private String msg;
    private Integer total;
    private Integer max_page;
    private Integer now_page;
  }
  
  @Data
  public static class GoodsData {
    private String goodsNo;  // 상품번호
    private String goodsNmFl;  // 상품명 타입 (d=기본, e=확장)
    private String goodsNm;  // 상품명
    private Integer scmNo;  // 공급사번호 (1=본사, 그외 공급사)
    private String cateCd;  // 대표 카테고리
    private String allCateCd; // 상품에 연결된 전체 카테고리
    private String goodsColor;  // 상품 색상
    private String goodsState;  // n: new, u: used, r: return
    private String brandCd;  // 브랜드코드
    private String makerNm;  // 제조사
    private String originNm;  // 원산지
    private String makeYmd;  // 제조일
    private String launchYmd;  // 출시일
    private String effectiveStartYmd;  // 유효일자 시작일
    private String effectiveEndYmd;  // 유효일자 종료
    private String goodsWeight;  // 상품 무게
    private Integer totalStock; // 재고량
    private String stockFl;  // 판매재고 (n=무한정판매, y=재고량에 따름)
    private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
    private Integer salesUnit;  // 묶음주문 단위
    private Integer minOrderCnt;  // 최소 구매수량
    private Integer maxOrderCnt;  // 최대 구매수량
    private String salesStartYmd;  // 판매 시작일
    private String salesEndYmd;  // 판매 종료일
    private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
    private BigDecimal goodsDiscount;  // 상품 할인 값
    private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
    private String goodsPriceString;  // 가격대체문구 (최대 30자)
    private BigDecimal goodsPrice;  // 판매가
    private BigDecimal fixedPrice;  // 정가
    private Integer deliverySno;  // 배송비 코드
    private String shortDescription;  // 짧은 설명 (250자 이내)
    private String goodsDescription;  // PC 쇼핑몰 상세설명
    private String goodsDescriptionMobile;  // 모바일 쇼핑몰 상세설명
    private String goodsSearchWord; // 검색 키워드
    private Integer orderCnt;  // 주문수
    private Integer hitCnt;  // 조회수
    private Integer reviewCnt;  // 상품평수
    private URL add1ImageData;
    private URL add2ImageData;
    private URL listImageData;  // 썸네일 이미지 정보
    private URL mainImageData;  // 리스트 이미지 정보
    private URL magnifyImageData;  // 확대이미지 정보
    private URL detailImageData;  // 상세 이미지 정보
    private String regDt;  // 상품등록일
    private String modDt;  // 상품수정일
    private String optionFl;  // 옵션 사용여부 (y|n)
    private String optionDisplayFl; // 옵션 노출 타입 (s: 일체형, d: 분리형)
    private String optionName; // 옵션명 (구분자 ^|^)
    private String optionTextFl; // 텍스트옵션여부 (y|n)
    private String addGoodsFl; // 추가상품여부 (y|n)
    private String relationFl;  // 관련상품 설(n: 사용안함 a: auto, m:manual)
    private String relationGoodsNo; // 관련상품코드 (구분자 ||)
    private String detailInfoDeliveryFl;  // no
    private String detailInfoDelivery; // 배송안내 (0=사용안함)
    private String detailInfoDeliveryDirectInput;
    private String detailInfoASFl;  // direct
    private String detailInfoAS; // AS안내 (0=사용안함)
    private String detailInfoASDirectInput;
    private String detailInfoRefundFl;  // direct
    private String detailInfoRefund; // 환불안내 (0=사용안함)
    private String detailInfoRefundDirectInput;
    private String detailInfoExchangeFl;  // no
    private String detailInfoExchange; // 교환안내 (0=사용안함)
    private String detailInfoExchangeDirectInput;
    private String goodsSellFl;
    private String goodsDisplayFl;


    @JacksonXmlElementWrapper(useWrapping = false)
    private List<OptionData> optionData;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GoodsMustInfoData> goodsMustInfoData;

  }

  @Data
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {
    "stepData"
  })
  public static class GoodsMustInfoData {
    @XmlElement(required = true)
    protected StepData stepData;
    @XmlAttribute(name = "idx")
    protected Byte idx;
  }

  @Data
  public static class StepData {
    @XmlElement(required = true)
    protected String infoTitle;
    @XmlElement(required = true)
    protected String infoValue;
    @XmlAttribute(name = "idx")
    protected Byte idx;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {
    "sno",
    "goodsNo",
    "optionNo",
    "optionValue1",
    "optionValue2",
    "optionValue3",
    "optionValue4",
    "optionValue5",
    "optionPrice",
    "optionCostPrice",
    "optionViewFl",
    "optionSellFl",
    "optionCode",
    "stockCnt",
    "optionMemo",
    "regDt",
    "modDt",
    "optionImage"
  })
  public static class OptionData {
    private int sno;  // 옵션고유번호
    private int goodsNo;  // 상품코드
    private int optionNo;  // 옵션순
    private String optionValue1;  // 1차 옵션명
    private String optionValue2;  // 2차 옵션명
    private String optionValue3;  // 3차 옵션명
    private String optionValue4;  // 4차 옵션명
    private String optionValue5;  // 5차 옵션명
    private BigDecimal optionPrice; // 옵션가
    private BigDecimal optionCostPrice;
    private String optionViewFl;  // 옵션 노출여부(y|n)
    private String optionSellFl;  // 옵션 판매여부
    private String optionCode;  // 옵션 자체코드
    private BigDecimal stockCnt;   // 재고
    private String optionMemo;
    private String regDt;
    private String modDt;
    private String optionImage;
  }
}