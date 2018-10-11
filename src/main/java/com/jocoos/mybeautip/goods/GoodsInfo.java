package com.jocoos.mybeautip.goods;

import java.io.IOException;
import java.net.URL;
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
  private String totalStock;  // 상품 재고
  private String stockFl;  // 판매재고 (n=무한정판매, y=재고량에 따름)
  private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
  private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
  private int goodsDiscount;  // 상품 할인 값
  private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
  private int goodsPrice;  // 판매가
  private int fixedPrice;  // 정가
  private URL listImageData;  // 썸네일 이미지 정보
  private Integer scmNo;
  private Integer likeCount;
  private Long likeId;
  private Integer deliverySno;
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

    if (goods.getGoodsMustInfo() != null) {
      ObjectMapper mapper = new ObjectMapper();
      List<GodoService.MustInfo> info = null;
      try {
        info = Arrays.asList(mapper.readValue(goods.getGoodsMustInfo(), GodoService.MustInfo[].class));
      } catch (IOException e) {
        this.goodsMustInfo = null;
      }
      this.goodsMustInfo = info;
    }

    this.deliveryInfo = StringUtils.isBlank(deliveryInfo) ? "" : deliveryInfo;
    this.refundInfo = StringUtils.isBlank(refundInfo) ? "" : refundInfo;
    this.asInfo = ""; // deprecated
    this.companyInfo = StringUtils.isBlank(companyInfo) ? "" : companyInfo;
  }
}