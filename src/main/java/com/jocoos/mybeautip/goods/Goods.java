package com.jocoos.mybeautip.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.net.URL;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "goods")
public class Goods {

    @Id
    private String goodsNo;  // 상품번호
    private Integer state;  // 상태 (0: 구매가능, 1:품절, 2: 구매불가(판매 안함), 3: 노출안함, 4: 삭제됨)
    private String goodsNm;  // 상품명
    private String cateCd;  // 대표 카테고리
    private String allCd;  // 대표 카테고리
    private String goodsColor;  // 상품 색상
    private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
    private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
    private Integer goodsDiscount;  // 상품 할인 값
    private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
    private Integer goodsPrice;  // 판매가
    private Integer fixedPrice;  // 정가
    private String goodsDescription;  // PC 쇼핑몰 상세설명
    private URL listImageData;  // 썸네일 이미지 정보
    private Integer reviewCnt;  // 상품평수
    private String shortDescription;  // 짧은 설명 (250자 이내)
    private String goodsDescriptionMobile;  // 모바일 쇼핑몰 상세설명
    private String goodsSearchWord; // 검색 키워드
    private String goodsDisplayFl;  // 노출여부
    private String goodsSellFl; // 판매여부
    private String goodsOpenDt;
    private String onlyAdultFl;
    private String goodsIconStartYmd;
    private String goodsIconEndYmd;
    private String goodsIconCdPeriod;
    private String goodsIconCd;
    private URL mainImageData;  // 리스트 이미지 정보
    private URL magnifyImageData;  // 확대이미지 정보
    private URL detailImageData;  // 상세 이미지 정보
    private String goodsNmFl;  // 상품명 타입 (d=기본, e=확장)
    private Integer scmNo;  // 공급사번호 (1=본사, 그외 공급사)
    private String goodsState;  // n: new, u: used, r: return
    private String brandCd;  // 브랜드코드
    private String makerNm;  // 제조사
    private String originNm;  // 원산지
    private String makeYmd;  // 제조일
    private String launchYmd;  // 출시일
    private String effectiveStartYmd;  // 유효일자 시작일
    private String effectiveEndYmd;  // 유효일자 종료
    private String goodsWeight;  // 상품 무게
    private Integer totalStock;  // 상품 재고
    private String stockFl;  // 판매재고 (n=무한정판매, y=재고량에 따름)
    private Integer salesUnit;  // 묶음주문 단위
    private Integer minOrderCnt;  // 최소 구매수량
    private Integer maxOrderCnt;  // 최대 구매수량
    private String salesStartYmd;  // 판매 시작일
    private String salesEndYmd;  // 판매 종료일
    private String goodsPriceString;  // 가격대체문구 (최대 30자)
    private Integer deliverySno;  // 배송비 코드
    private String deliveryFixFl;  // 배송비 정책 방식 (fixed=고정, free=무료, price=금액별, count=수량별, weight=무게별)
    private Integer baseShipping; // 기본배송비
    private String deliveryMethod;  // 배송비 정책 문구
    private Integer orderCnt;  // 주문수
    private Integer hitCnt;  // 조회수
    private URL add1ImageData;
    private URL add2ImageData;
    private String videoUrl;
    private String goodsMustInfo;
    private String optionFl;
    private String optionName;
    @Column(nullable = false)
    private int likeCount;
    @Column
    @CreatedDate
    private Date createdAt;
    @Column
    @LastModifiedDate
    private Date modifiedAt;
    @Column
    @LastModifiedDate
    private Date deletedAt;

    public enum GoodsState {
        AVAILABLE, SOLDOUT, NO_SALE, NO_DISPLAY, DELETED
    }
}