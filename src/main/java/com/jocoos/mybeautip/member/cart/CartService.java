package com.jocoos.mybeautip.member.cart;

import java.net.URL;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.store.StoreRepository;

@Slf4j
@Service
public class CartService {
  @Value("${mybeautip.point.earn-ratio}")
  private int pointRatio;

  @Value("${mybeautip.shipping.fixed}")
  private int fixedShipping;

  private final MemberService memberService;
  private final CartRepository cartRepository;
  private final StoreRepository storeRepository;
  private final DeliveryChargeRepository deliveryChargeRepository;
  private final DeliveryChargeDetailRepository deliveryChargeDetailRepository;

  public CartService(MemberService memberService,
                     CartRepository cartRepository,
                     StoreRepository storeRepository,
                     DeliveryChargeRepository deliveryChargeRepository,
                     DeliveryChargeDetailRepository deliveryChargeDetailRepository) {
    this.memberService = memberService;
    this.cartRepository = cartRepository;
    this.storeRepository = storeRepository;
    this.deliveryChargeRepository = deliveryChargeRepository;
    this.deliveryChargeDetailRepository = deliveryChargeDetailRepository;
  }

  public CartInfo getCartItemList() {
    Map<Integer, List<CartDelivery>> storeMap = new LinkedHashMap<>();  // key: storeId
    Map<Integer, List<CartItem>> deliveryMap = new LinkedHashMap<>();   // key: deliverySno

    List<Cart> list = cartRepository.findAllByCreatedByIdOrderByModifiedAtDesc(memberService.currentMemberId());
    List<CartItem> items;

    for (Cart c : list) {
      int deliverySno = c.getGoods().getDeliverySno();
      if (deliveryMap.containsKey(deliverySno)) {
        items = deliveryMap.get(deliverySno);
      } else {
        items = new ArrayList<>();
      }
      items.add(new CartItem(c));
      deliveryMap.put(deliverySno, items);
    }

    List<CartDelivery> deliveries;
    for (Integer deliverySno : deliveryMap.keySet()) {
      int storeId = deliveryMap.get(deliverySno).get(0).getGoods().getScmNo();
      if (storeMap.containsKey(storeId)) {
        deliveries = storeMap.get(storeId);
      } else {
        deliveries = new ArrayList<>();
      }

      deliveries.add(new CartDelivery(deliverySno, deliveryMap.get(deliverySno)));
      storeMap.put(storeId, deliveries);
    }

    List<CartStore> stores = new ArrayList<>();
    for (Integer storeId : storeMap.keySet()) {
      stores.add(new CartStore(storeId, storeRepository.getOne(storeId).getName(), storeMap.get(storeId)));
    }

    // Calculate count, price and shipping
    int totalCount, totalCheckedCount, totalFixedPrice, totalPrice, totalShipping;
    totalCount = totalCheckedCount = totalFixedPrice = totalPrice = totalShipping = 0;

    for (CartStore store : stores) {
      int storeCount, storeCheckedCount, storeFixedPrice, storePrice, storeShipping;
      storeCount = storeCheckedCount = storeFixedPrice = storePrice = storeShipping = 0;
      for (CartDelivery delivery : store.getDeliveries()) {
        int count, checkedCount, fixedPrice, price;
        count = checkedCount = fixedPrice = price = 0;
        for (CartItem item : delivery.getItems()) {
          count += 1;
          if (item.getChecked()) {
            checkedCount += 1;
            fixedPrice += item.getFixedPrice();
            price += item.getPrice();
          }
        }
        delivery.setCount(count);
        delivery.setCheckedCount(checkedCount);
        delivery.setFixedPrice(fixedPrice);
        delivery.setPrice(price);

        DeliveryCharge deliveryCharge = deliveryChargeRepository.getOne(delivery.getDeliverySno());
        BeanUtils.copyProperties(deliveryCharge, delivery);
        switch (delivery.getFixFl()) {
          case "free":
            delivery.setShipping(0);
            break;

          case "count":
            delivery.setShipping(deliveryChargeDetailRepository.findByDeliveryChargeIdAndUnitStartLessThanEqualAndUnitEndGreaterThan(
              deliveryCharge.getId(), count, count).map(DeliveryChargeDetail::getPrice).orElse(0));
            break;

          case "price":
            delivery.setShipping(deliveryChargeDetailRepository.findByDeliveryChargeIdAndUnitStartLessThanEqualAndUnitEndGreaterThan(
              deliveryCharge.getId(), price, price).map(DeliveryChargeDetail::getPrice).orElse(0));
            break;

          case "fixed":
          default:
            delivery.setShipping(fixedShipping);
            break;
        }
        storeCount += delivery.getCount();
        storeCheckedCount += delivery.getCheckedCount();
        storeFixedPrice += delivery.getFixedPrice();
        storePrice += delivery.getPrice();
        storeShipping += delivery.getShipping();
      }
      store.setCount(storeCount);
      store.setCheckedCount(storeCheckedCount);
      store.setFixedPrice(storeFixedPrice);
      store.setPrice(storePrice);
      store.setShipping(storeShipping);
      totalCount += storeCount;
      totalCheckedCount += storeCheckedCount;
      totalFixedPrice += storeFixedPrice;
      totalPrice += storePrice;
      totalShipping += storeShipping;
    }

    return CartInfo.builder()
      .stores(stores)
      .totalCount(totalCount)
      .checkedCount(totalCheckedCount)
      .fixedPriceAmount(totalFixedPrice)
      .discountAmount(totalFixedPrice - totalPrice)
      .priceAmount(totalPrice)
      .shippingAmount(totalShipping)
      .build();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CartInfo {
    private Integer totalCount; // 전체 장바구니 아이템 개수
    private Integer checkedCount; // 체크된 장바구니 아이템 개수
    private Integer fixedPriceAmount;  // 정가(상품 정가 + 옵션가) 합계
    private Integer discountAmount; // 상품할인 합계
    private Integer priceAmount;  // 판매가(상품 판매가 + 옵션가)의 합계
    private Integer shippingAmount; // 배송비 합계
    private List<CartStore> stores; // 스토어 리스트
  }

  @Data
  static class CartStore {
    @JsonIgnore private Integer storeId; // 스토어 이름
    private String storeName; // 스토어 이름
    @JsonIgnore private Integer count;  // 스토어 별 아이템 개수
    @JsonIgnore private Integer checkedCount; // 스토어 별 체크된 장바구니 아이템 개수
    @JsonIgnore private Integer fixedPrice; // 스토어 별 정가(상품 정가 + 옵션가)의 소계
    private Integer price;  // 스토어 별 판매가(상품 판매가 + 옵션가)의 소계
    private Integer shipping; // 스토어 별 배송비 소계
    private List<CartDelivery> deliveries;  // 배송정책 리스트

    CartStore(Integer storeId, String storeName, List<CartDelivery> deliveries) {
      this.storeId = storeId;
      this.storeName = storeName;
      this.deliveries = deliveries;
    }
  }

  @Data
  static class CartDelivery {
    private Integer deliverySno; // 배송정책 번호
    @JsonIgnore private String fixFl; // 배송비 부과 방식: "free", "fixed", "price", "count"
    private String method;  // 배송정책 문자열
    @JsonIgnore private Integer count;  // 배송정책 별 아이템 개수
    @JsonIgnore private Integer checkedCount; // 배송정책 별 체크된 아이템 개수
    @JsonIgnore private Integer fixedPrice; // 배송정책 별 정가(상품 정가 + 옵션가)의 소계
    private Integer price;  // 배송정책 별 판매가(상품 판매가 + 옵션가)의 소계
    private Integer shipping; // 배송정책 별 배송비 소계
    private List<CartItem> items;  // 아이템 리스트

    CartDelivery(Integer deliverySno, List<CartItem> items) {
      this.deliverySno = deliverySno;
      this.items = items;
    }
  }

  @Data
  static class CartItem {
    private Long id;
    private Boolean checked;
    private CartGoodsInfo goods;
    private CartOptionInfo option;
    private Integer quantity;
    @JsonIgnore private Integer fixedPrice; // (상품정가 + 옵션가) * 수량
    private Integer price;  // (상품 판매가 + 옵션가) * 수량
    private Date createdAt;
    private Date modifiedAt;

    CartItem(Cart cart) {
      BeanUtils.copyProperties(cart, this);
      this.goods = new CartGoodsInfo(cart.getGoods());
      if (cart.getOption() != null) {
        this.option = new CartOptionInfo(cart.getOption());
      }

      int goodsFixedPrice = (cart.getGoods().getFixedPrice() > 0) ? cart.getGoods().getFixedPrice() : cart.getGoods().getGoodsPrice();
      int perItemFixedPrice = goodsFixedPrice + ((option != null) ? option.getOptionPrice() : 0);
      int perItemPrice = cart.getGoods().getGoodsPrice() + ((option != null) ? option.getOptionPrice() : 0);

      this.fixedPrice = perItemFixedPrice * cart.getQuantity();
      this.price = perItemPrice * cart.getQuantity();
    }
  }

  @Data
  static class CartGoodsInfo {
    private String goodsNo;
    private String goodsNm;
    private URL listImageData;  // 썸네일 이미지 정보
    private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
    private String stockFl;  // 판매재고 (n=무한정판매, y=재고량에 따름)
    private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
    private Integer goodsDiscount;  // 상품 할인 값
    private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
    private Integer goodsPrice;  // 판매가
    private Integer fixedPrice;  // 정가
    @JsonIgnore private Integer deliverySno;  // 배송비 정책 번호
    @JsonIgnore private Integer scmNo;  // 공급사 번호

    CartGoodsInfo(Goods goods) {
      BeanUtils.copyProperties(goods, this);
    }
  }

  @Data
  static class CartOptionInfo {
    private Integer optionNo;
    private String optionValue;
    private Integer optionPrice;
    private Integer stockCnt;

    CartOptionInfo(GoodsOption option) {
      this.optionNo = option.getOptionNo();
      this.optionValue = option.getOptionValue1();
      this.optionPrice = option.getOptionPrice();
      this.stockCnt = option.getStockCnt();
    }
  }
}
