package com.jocoos.mybeautip.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;

@Entity
@Table(name = "goods")
@Data
public class Goods {
	@Id
	private String goodsNo;	// 상품번호
	private String goodsNm;	// 상품명
	private String cateCd;	// 대표 카테고리
	private String goodsColor;	// 상품 색상
	private String soldOutFl;	// 품절상태 (n= 정상, y=품절(수동))
	private String goodsDiscountFl;	// 상품 할인 설정 ( y=사용함, n=사용안함)
	private BigDecimal goodsDiscount;	// 상품 할인 값
	private String goodsDiscountUnit;	// 상품 할인 단위 ( percent=%, price=원)
	private BigDecimal goodsPrice;	// 판매가
	private BigDecimal fixedPrice;	// 정가
	private String shortDescription;	// 짧은 설명 (250자 이내)
	private String goodsDescription;	// PC 쇼핑몰 상세설명
	private String goodsDescriptionMobile;	// 모바일 쇼핑몰 상세설명
	private Integer reviewCnt;	// 상품평수
	private URL listImageData;	// 썸네일 이미지 정보
	private URL mainImageData;	// 리스트 이미지 정보
	private URL magnifyImageData;	// 확대이미지 정보
	private URL detailImageData;	// 상세 이미지 정보
	private String regDt;	// 상품등록일
	private String modDt;	// 상품수정일
	
	@JsonIgnore private String goodsNmFl;	// 상품명 타입 (d=기본, e=확장)
	@JsonIgnore private Integer scmNo;	// 공급사번호 (1=본사, 그외 공급사)
	@JsonIgnore private String goodsState;	// n: new, u: used, r: return
	@JsonIgnore private String brandCd;	// 브랜드코드
	@JsonIgnore private String makerNm;	// 제조사
	@JsonIgnore private String originNm;	// 원산지
	@JsonIgnore private String makeYmd;	// 제조일
	@JsonIgnore private String launchYmd;	// 출시일
	@JsonIgnore private String effectiveStartYmd;	// 유효일자 시작일
	@JsonIgnore private String effectiveEndYmd;	// 유효일자 종료
	@JsonIgnore private String goodsWeight;	// 상품 무게
	@JsonIgnore private String stockFl;	// 판매재고 (n=무한정판매, y=재고량에 따름)
	@JsonIgnore private Integer salesUnit;	// 묶음주문 단위
	@JsonIgnore private Integer minOrderCnt;	// 최소 구매수량
	@JsonIgnore private Integer maxOrderCnt;	// 최대 구매수량
	@JsonIgnore private String salesStartYmd;	// 판매 시작일
	@JsonIgnore private String salesEndYmd;	// 판매 종료일
	@JsonIgnore private String goodsPriceString;	// 가격대체문구 (최대 30자)
	@JsonIgnore private Integer deliverySno;	// 배송비 코드
	@JsonIgnore private Integer orderCnt;	// 주문수
	@JsonIgnore private Integer hitCnt;	// 조회수
	@JsonIgnore private URL add1ImageData;
	@JsonIgnore private URL add2ImageData;
	@JsonIgnore private Long createdAt;
	@JsonIgnore private Long updatedAt;
}