package com.jocoos.mybeautip.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

@Data
@NoArgsConstructor
public class GoodsInfo {
  private String goodsNo;  // 상품번호
  private String goodsNm;  // 상품명
  private String cateCd;  // 대표 카테고리
  private String goodsColor;  // 상품 색상
  private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
  private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
  private BigDecimal goodsDiscount;  // 상품 할인 값
  private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
  private int goodsPrice;  // 판매가
  private int fixedPrice;  // 정가
  private String goodsDescription;  // PC 쇼핑몰 상세설명
  private URL listImageData;  // 썸네일 이미지 정보
  
  @JsonIgnore
  private Date createdAt;
  @JsonIgnore
  private Date modifiedAt;
  
  public GoodsInfo(Goods goods) {
    this.goodsNo = goods.getGoodsNo();
    this.goodsNm = goods.getGoodsNm();
    this.cateCd = goods.getCateCd();
    this.goodsColor = goods.getGoodsColor();
    this.soldOutFl = goods.getSoldOutFl();
    this.goodsDiscountFl = goods.getGoodsDiscountFl();
    this.goodsDiscount = goods.getGoodsDiscount();
    this.goodsDiscountUnit = goods.getGoodsDiscountUnit();
    this.goodsPrice = toIntPrice(goods.getGoodsPrice());
    this.fixedPrice = toIntPrice(goods.getFixedPrice());
    this.goodsDescription = goods.getGoodsDescription();
    this.listImageData = goods.getListImageData();
  }

  private int toIntPrice(BigDecimal price) {
    if (price != null) {
      return price.intValue();
    }
    return 0;
  }
}