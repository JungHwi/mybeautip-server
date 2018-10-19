package com.jocoos.mybeautip.goods;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.godo.GodoService;

@Data
@NoArgsConstructor
public class GoodsInfo {
  private String goodsNo;  // 상품번호
  private String goodsNm;  // 상품명
  private String cateCd;  //  대표 카테고리
  private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
  private String stockFl;  // 판매재고 (n=무한정판매, y=재고량에 따름)
  private Integer totalStock;  // 상품 재고
  private Integer salesUnit;  // 묶음주문 단위
  private Integer minOrderCnt;  // 최소 구매수량, 최대 구매수량 미입력시 적용불가
  private Integer maxOrderCnt;  // 최대 구매 수량, 최소 구매수량 미입력시 적용불가
  private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
  private Integer goodsDiscount;  // 상품 할인 값
  private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
  private Integer goodsPrice;  // 판매가
  private Integer fixedPrice;  // 정가
  private URL listImageData;  // 썸네일 이미지 정보
  private Integer scmNo;  // 공급자 번호
  private Integer likeCount;
  private Long likeId;
  private Integer deliverySno;  // 배송비 정책 번호
  private String goodsSearchWord; // 검색 키워드
  private List<GodoService.MustInfo> goodsMustInfo;
  private Integer relatedVideoTotalCount; // 관련 방송(동영상 리뷰) 전체 개수
  private String deliveryInfo;
  private String refundInfo;
  private String asInfo;
  private String companyInfo;
  private String optionFl;    // 옵션 사용여부
  private String optionName;  // 옵션 이름
  private String detailRef;   // 상품 상세 페이지를 볼 수 있는 Html 주소
  private String optionRef;   // 상품 옵션 데이터를 볼 수 있는 주소
  private String relatedGoodsRef;   // 관련 상품을 볼 수 있는 주소
  private String relatedVideoRef;   // 관련 방송(동영상 리뷰)를 볼 수 있는 주소

  @JsonIgnore
  private Date createdAt;
  @JsonIgnore
  private Date modifiedAt;

  public GoodsInfo(Goods goods, Long likeId, Integer relatedVideoTotalCount,
                   String deliveryInfo, String refundInfo, String companyInfo) {
    BeanUtils.copyProperties(goods, this);
    this.likeId = likeId;
    this.relatedVideoTotalCount = relatedVideoTotalCount;
    this.detailRef = String.format("/api/1/goods/%s/details", goodsNo);
    this.relatedGoodsRef = String.format("/api/1/goods/%s/related-goods", goodsNo);
    this.relatedVideoRef = (relatedVideoTotalCount > 0) ? String.format("/api/1/goods/%s/videos", goodsNo) : "";
    this.optionRef = ("y".equalsIgnoreCase(goods.getOptionFl())) ? String.format("/api/1/goods/%s/options", goodsNo) : "";

    if (goods.getGoodsMustInfo() == null) {
      this.goodsMustInfo = new ArrayList<>();
    } else {
      ObjectMapper mapper = new ObjectMapper();
      try {
        this.goodsMustInfo = Arrays.asList(mapper.readValue(goods.getGoodsMustInfo(), GodoService.MustInfo[].class));
      } catch (IOException e) {
        this.goodsMustInfo = new ArrayList<>();
      }
    }

    this.deliveryInfo = StringUtils.isBlank(deliveryInfo) ? "" : deliveryInfo;
    this.refundInfo = StringUtils.isBlank(refundInfo) ? "" : refundInfo;
    this.asInfo = ""; // deprecated
    this.companyInfo = StringUtils.isBlank(companyInfo) ? "" : companyInfo;
  }
}