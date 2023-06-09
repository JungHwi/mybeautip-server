package com.jocoos.mybeautip.member.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.legacy.store.LegacyStore;
import com.jocoos.mybeautip.legacy.store.LegacyStoreRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.CartController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.*;

@Slf4j
@Service
public class CartService {
    private static final String CART_TOO_MANY_ITEMS = "cart.too_many_items";
    private static final String CART_GOODS_SOLD_OUT = "cart.goods_sold_out";
    private static final String CART_OPTION_SOLD_OUT = "cart.option_sold_out";
    private static final String CART_INVALID_QUANTITY = "cart.invalid_quantity";
    private static final String GOODS_NOT_FOUND = "goods.not_found";
    private static final String OPTION_NOT_FOUND = "option.not_found";
    private static final String CART_ITEM_NOT_FOUND = "cart.item_not_found";
    private static final String STORE_NOT_FOUND = "store.not_found";
    private final MessageService messageService;
    private final TimeSaleService timeSaleService;
    private final GoodsRepository goodsRepository;
    private final GoodsOptionRepository goodsOptionRepository;
    private final CartRepository cartRepository;
    private final LegacyStoreRepository legacyStoreRepository;
    private final DeliveryChargeRepository deliveryChargeRepository;
    private final DeliveryChargeDetailRepository deliveryChargeDetailRepository;
    private final DeliveryChargeOptionRepository deliveryChargeOptionRepository;
    @Value("${mybeautip.point.earn-ratio}")
    private int pointRatio;
    @Value("${mybeautip.shipping.fixed}")
    private int fixedShipping;

    public CartService(MessageService messageService,
                       TimeSaleService timeSaleService,
                       GoodsRepository goodsRepository,
                       GoodsOptionRepository goodsOptionRepository,
                       DeliveryChargeOptionRepository deliveryChargeOptionRepository,
                       CartRepository cartRepository,
                       LegacyStoreRepository legacyStoreRepository,
                       DeliveryChargeRepository deliveryChargeRepository,
                       DeliveryChargeDetailRepository deliveryChargeDetailRepository) {
        this.messageService = messageService;
        this.timeSaleService = timeSaleService;
        this.goodsRepository = goodsRepository;
        this.goodsOptionRepository = goodsOptionRepository;
        this.deliveryChargeOptionRepository = deliveryChargeOptionRepository;
        this.cartRepository = cartRepository;
        this.legacyStoreRepository = legacyStoreRepository;
        this.deliveryChargeRepository = deliveryChargeRepository;
        this.deliveryChargeDetailRepository = deliveryChargeDetailRepository;
    }

    private static boolean isSoldOut(CartGoodsInfo goods, GoodsOption option) {
        if ("n".equals(option.getOptionSellFl())) { // 옵션 판매안함
            return true;
        }

        if ("y".equals(goods.getSoldOutFl())) { // 상품 품절 플래그
            return true;
        }

        if ("y".equals(goods.getStockFl()) && goods.getTotalStock() <= 0) { // 재고량에 따름, 총 재고량 부족
            return true;
        }

        // 재고량에 따름, 옵션 재고량 부족
        return "y".equals(goods.getStockFl()) && option.getStockCnt() <= 0;

    }

    public CartInfo getCartItemList(long memberId) {
        List<Cart> list = cartRepository.findAllByCreatedByIdOrderByModifiedAtDesc(memberId);
        return getCartItemList(list);
    }

    public CartInfo getCartItemList(List<Cart> list) {
        return getCartItemList(list, TimeSaleCondition.createGeneral());
    }

    public CartInfo getCartItemList(List<Cart> list, TimeSaleCondition timeSaleCondition) {
        timeSaleService.applyTimeSaleForCart(list, timeSaleCondition);
        return getCartItemList0(list);
    }

    private CartInfo getCartItemList0(List<Cart> list) {
        Map<Integer, List<CartDelivery>> storeMap = new LinkedHashMap<>();  // key: storeId
        Map<Integer, List<CartItem>> deliveryMap = new LinkedHashMap<>();   // key: deliverySno

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
            stores.add(new CartStore(storeId, legacyStoreRepository.getById(storeId).getName(), storeMap.get(storeId)));
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
                    if (item.getChecked() && item.getValid()) {
                        checkedCount += 1;
                        fixedPrice += item.getFixedPrice();
                        price += item.getPrice();
                    }
                }
                delivery.setCount(count);
                delivery.setCheckedCount(checkedCount);
                delivery.setFixedPrice(fixedPrice);
                delivery.setPrice(price);

                DeliveryCharge deliveryCharge = deliveryChargeRepository.getById(delivery.getDeliverySno());
                BeanUtils.copyProperties(deliveryCharge, delivery);

                if (checkedCount > 0) {
                    switch (delivery.getFixFl()) {
                        case "free":
                            delivery.setShipping(0);
                            break;

                        case "count":
                            delivery.setShipping(deliveryChargeDetailRepository.findByDeliveryChargeIdAndUnitStartLessThanEqualAndUnitEndGreaterThan(
                                    deliveryCharge.getId(), checkedCount, checkedCount).map(DeliveryChargeDetail::getPrice).orElse(0));
                            break;

                        case "price":
                            delivery.setShipping(deliveryChargeDetailRepository.findByDeliveryChargeIdAndUnitStartLessThanEqualAndUnitEndGreaterThan(
                                    deliveryCharge.getId(), price, price).map(DeliveryChargeDetail::getPrice).orElse(0));
                            break;

                        case "fixed":
                        default:
                            delivery.setShipping(deliveryChargeDetailRepository.findByDeliveryChargeIdAndUnitStartLessThanEqualAndUnitEndGreaterThan(
                                    deliveryCharge.getId(), price, price).map(DeliveryChargeDetail::getPrice).orElse(fixedShipping));
                            break;
                    }
                } else {
                    delivery.setShipping(0);
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
                .pointRatio(pointRatio)
                .build();
    }

    public int updateShippingAmount(CartInfo cartInfo, int areaShipping) {
        // TODO: change logic if table has too much data
        List<DeliveryChargeOption> deliveryChargeOptions = deliveryChargeOptionRepository.findAll();

        int shippingAmount = cartInfo.shippingAmount;
        int extraFeeCount = 0;
        for (CartStore store : cartInfo.getStores()) {
            for (CartDelivery delivery : store.getDeliveries()) {
                if (!foundDeliveryChargeOption(delivery.deliverySno, deliveryChargeOptions)) {
                    extraFeeCount = extraFeeCount + 1;
                }
            }
        }
        return shippingAmount + (extraFeeCount * areaShipping);
    }

    private boolean foundDeliveryChargeOption(int deliveryChargeId, List<DeliveryChargeOption> deliveryChargeOptions) {
        for (DeliveryChargeOption chargeOption : deliveryChargeOptions) {
            if (chargeOption.getDeliveryChargeId() == deliveryChargeId) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void addItems(CartController.AddCartRequest request, long memberId, String lang) {
        for (CartController.CartItemRequest requestItem : request.getItems()) {
            Cart item = getValidCartItem(requestItem.getGoodsNo(), requestItem.getOptionNo(), requestItem.getQuantity(), lang);

            Optional<Cart> optionalCart;
            if (item.getOption() == null) {
                optionalCart = cartRepository.findByGoodsGoodsNoAndCreatedById(item.getGoods().getGoodsNo(), memberId);
            } else {
                optionalCart = cartRepository.findByGoodsGoodsNoAndOptionOptionNoAndCreatedById(
                        item.getGoods().getGoodsNo(), item.getOption().getOptionNo(), memberId);
            }

            if (optionalCart.isPresent()) { // Update quantity
                Cart cart = optionalCart.get();
                checkQuantityValidity(cart.getGoods(), cart.getOption(), cart.getQuantity() + item.getQuantity(), lang);
                cart.setQuantity(cart.getQuantity() + item.getQuantity());
                update(cart);
            } else {  // Insert new item
                save(item);
            }
        }
    }

    @Transactional
    public void updateItem(long id, long memberId, CartController.UpdateCartRequest request, String lang) {
        Cart item = cartRepository.findByIdAndCreatedById(id, memberId)
                .map(cart -> {
                    if (request.getQuantity() != null && request.getQuantity() > 0) {
                        cart.setQuantity(request.getQuantity());
                    }
                    if (request.getChecked() != null) {
                        cart.setChecked(request.getChecked());
                    }
                    return cart;
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(CART_ITEM_NOT_FOUND, lang)));

        update(item);
    }

    @Transactional
    public void removeItem(long id, long memberId, String lang) {
        cartRepository.findByIdAndCreatedById(id, memberId)
                .map(cart -> {
                    cartRepository.delete(cart);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(CART_ITEM_NOT_FOUND, lang)));
    }

    /**
     * Update checked value to all cart items (all checked or all unchecked)
     */
    @Transactional
    public void updateAllItems(CartController.UpdateCartRequest request, Member me) {
        boolean checked = (request.getChecked() == null) ? true : request.getChecked();
        cartRepository.updateAllChecked(checked, me);
    }

    private Cart getValidCartItem(String goodsNo, int optionNo, int quantity, String lang) {
        Goods goods = goodsRepository.findByGoodsNoAndStateLessThanEqual(goodsNo, Goods.GoodsState.NO_SALE.ordinal())
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(GOODS_NOT_FOUND, lang)));

        if ("y".equals(goods.getSoldOutFl())) { // 품절 플래그
            throw new BadRequestException(messageService.getMessage(CART_GOODS_SOLD_OUT, lang));
        }

        if ("y".equals(goods.getStockFl()) && quantity > goods.getTotalStock()) { // 재고량에 따름, 총 재고량 추가
            throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
        }

        if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity < goods.getMinOrderCnt()) { // 최소구매수량 미만
            throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
        }

        if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity > goods.getMaxOrderCnt()) { // 최대구매수량 초과
            throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
        }

        GoodsOption option = null;
        if ("y".equals(goods.getOptionFl())) {
            option = goodsOptionRepository.findByGoodsNoAndOptionNoAndOptionViewFl(Integer.parseInt(goodsNo), optionNo, "y")
                    .orElseThrow(() -> new NotFoundException(messageService.getMessage(OPTION_NOT_FOUND, lang)));
            if ("n".equals(option.getOptionSellFl())) { // 옵션 판매안함
                throw new BadRequestException(messageService.getMessage(CART_OPTION_SOLD_OUT, lang));
            }
            if ("y".equals(goods.getStockFl()) && quantity > option.getStockCnt()) { // 재고량에 따름
                throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
            }
        } else if (optionNo != 0) {
            throw new NotFoundException(messageService.getMessage(OPTION_NOT_FOUND, lang));
        }

        LegacyStore legacyStore = legacyStoreRepository.findById(goods.getScmNo())
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(STORE_NOT_FOUND, lang)));

        return new Cart(goods, option, legacyStore, quantity);
    }

    private void checkQuantityValidity(Goods goods, GoodsOption option, int quantity, String lang) {
        if ("y".equals(goods.getSoldOutFl())) { // 품절 플래그
            throw new BadRequestException(messageService.getMessage(CART_GOODS_SOLD_OUT, lang));
        }

        if ("y".equals(goods.getStockFl()) && quantity > goods.getTotalStock()) { // 재고량에 따름, 총 재고량 추가
            throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
        }

        if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity < goods.getMinOrderCnt()) { // 최소구매수량 미만
            throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
        }

        if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity > goods.getMaxOrderCnt()) { // 최대구매수량 초과
            throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
        }

        if ("y".equals(goods.getOptionFl())) {
            if ("n".equals(option.getOptionSellFl())) { // 옵션 판매안함
                throw new BadRequestException(messageService.getMessage(CART_OPTION_SOLD_OUT, lang));
            }
            if ("y".equals(goods.getStockFl()) && quantity > option.getStockCnt()) { // 재고량에 따름
                throw new BadRequestException(messageService.getMessage(CART_INVALID_QUANTITY, lang));
            }
        }
    }

    /**
     * Wrap method to manipulate modifiedAt manually
     */
    @Transactional
    public Cart save(Cart cart) {
        cart.setModifiedAt(new Date());
        return cartRepository.save(cart);
    }

    /**
     * Wrap method to manipulate modifiedAt manually
     * This is used when update without modifiedAt change
     */
    @Transactional
    public Cart update(Cart cart) {
        return cartRepository.save(cart);
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
        private Integer pointRatio; // 포인트 적립률
        private List<CartStore> stores; // 스토어 리스트
    }

    @Data
    static class CartStore {
        @JsonIgnore
        private Integer storeId; // 스토어 이름
        private String storeName; // 스토어 이름
        @JsonIgnore
        private Integer count;  // 스토어 별 아이템 개수
        @JsonIgnore
        private Integer checkedCount; // 스토어 별 체크된 장바구니 아이템 개수
        @JsonIgnore
        private Integer fixedPrice; // 스토어 별 정가(상품 정가 + 옵션가)의 소계
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
        @JsonIgnore
        private String fixFl; // 배송비 부과 방식: "free", "fixed", "price", "count"
        private String method;  // 배송정책 문자열
        @JsonIgnore
        private Integer count;  // 배송정책 별 아이템 개수
        @JsonIgnore
        private Integer checkedCount; // 배송정책 별 체크된 아이템 개수
        @JsonIgnore
        private Integer fixedPrice; // 배송정책 별 정가(상품 정가 + 옵션가)의 소계
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
        private Boolean valid = true;
        private CartGoodsInfo goods;
        private CartOptionInfo option;
        private Integer quantity;
        @JsonIgnore
        private Integer fixedPrice; // (상품정가 + 옵션가) * 수량
        private Integer price;  // (상품 판매가 + 옵션가) * 수량
        private Date createdAt;
        private Date modifiedAt;

        CartItem(Cart cart) {
            BeanUtils.copyProperties(cart, this);
            this.goods = new CartGoodsInfo(cart.getGoods());
            if (cart.getOption() != null) {
                this.option = new CartOptionInfo(cart.getOption(), isSoldOut(goods, cart.getOption()));
            }

            int goodsFixedPrice = (cart.getGoods().getFixedPrice() > 0) ? cart.getGoods().getFixedPrice() : cart.getGoods().getGoodsPrice();
            int perItemFixedPrice = goodsFixedPrice + ((option != null) ? option.getOptionPrice() : 0);
            int perItemPrice = cart.getGoods().getGoodsPrice() + ((option != null) ? option.getOptionPrice() : 0);

            this.fixedPrice = perItemFixedPrice * cart.getQuantity();
            this.price = perItemPrice * cart.getQuantity();

            if ("y".equals(goods.getSoldOutFl())
                    || ("y".equals(goods.getStockFl()) && this.quantity > goods.getTotalStock())
                    || (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && this.quantity < goods.getMinOrderCnt())
                    || (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && this.quantity > goods.getMaxOrderCnt())
                    || (option != null && "y".equals(goods.getStockFl()) && this.quantity > option.getStockCnt())
                    || (option != null && "y".equals(goods.getStockFl()) && "n".equals(option.getOptionSellFl()))) {
                this.valid = false;
            }

            // createdAt and modifiedAt can be null when just calculate cart info without save (/members/me/carts/now API)
            if (this.createdAt == null) {
                this.createdAt = new Date();
            }
            if (this.modifiedAt == null) {
                this.modifiedAt = new Date();
            }
        }
    }

    @Data
    static class CartGoodsInfo {
        private String goodsNo;
        private Integer state;  // 상태 (0: 구매가능, 1:품절, 2: 구매불가(판매 안함), 3: 노출안함, 4: 삭제됨)
        private String goodsNm;
        private URL listImageData;  // 썸네일 이미지 정보
        private String soldOutFl;  // 품절상태 (n= 정상, y=품절(수동))
        private String stockFl;  // 판매재고 (n=무한정판매, y=재고량에 따름)
        private String goodsDiscountFl;  // 상품 할인 설정 ( y=사용함, n=사용안함)
        private Integer goodsDiscount;  // 상품 할인 값
        private String goodsDiscountUnit;  // 상품 할인 단위 ( percent=%, price=원)
        private Integer goodsPrice;  // 판매가
        private Integer fixedPrice;  // 정가
        @JsonIgnore
        private Integer deliverySno;  // 배송비 정책 번호
        @JsonIgnore
        private Integer scmNo;  // 공급사 번호
        @JsonIgnore
        private Integer totalStock; // 총 재고
        @JsonIgnore
        private Integer minOrderCnt;  // 최소구매수량
        @JsonIgnore
        private Integer maxOrderCnt;  // 쵀대구매수량

        CartGoodsInfo(Goods goods) {
            BeanUtils.copyProperties(goods, this);
        }
    }

    @Data
    static class CartOptionInfo {
        private Integer optionNo;
        private String optionValue;
        private String optionValue1;
        private String optionValue2;
        private Integer optionPrice;
        private Integer stockCnt;
        @JsonIgnore
        private String optionSellFl;
        private Boolean soldOut;

        CartOptionInfo(GoodsOption option, boolean soldOut) {
            BeanUtils.copyProperties(option, this);
            this.optionValue = option.getOptionValue1();
            this.soldOut = soldOut;
        }
    }
}
