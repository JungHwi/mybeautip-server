package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.legacy.store.LegacyStore;
import com.jocoos.mybeautip.legacy.store.LegacyStoreRepository;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.member.address.AddressRepository;
import com.jocoos.mybeautip.member.cart.Cart;
import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.cart.CartService;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/carts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartController {

    private static final String CART_TOO_MANY_ITEMS = "cart.too_many_items";
    private static final String CART_GOODS_SOLD_OUT = "cart.goods_sold_out";
    private static final String CART_OPTION_SOLD_OUT = "cart.option_sold_out";
    private static final String CART_INVALID_QUANTITY = "cart.invalid_quantity";
    private static final String GOODS_NOT_FOUND = "goods.not_found";
    private static final String OPTION_NOT_FOUND = "option.not_found";
    private static final String CART_ITEM_NOT_FOUND = "cart.item_not_found";
    private static final String STORE_NOT_FOUND = "store.not_found";
    private static final String ADDRESS_NOT_FOUND = "address.not_found";
    private final LegacyMemberService legacyMemberService;
    private final CartService cartService;
    private final MessageService messageService;
    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final GoodsOptionRepository goodsOptionRepository;
    private final LegacyStoreRepository legacyStoreRepository;
    private final AddressRepository addressRepository;

    public CartController(LegacyMemberService legacyMemberService,
                          CartService cartService,
                          MessageService messageService,
                          CartRepository cartRepository,
                          GoodsRepository goodsRepository,
                          GoodsOptionRepository goodsOptionRepository,
                          LegacyStoreRepository legacyStoreRepository,
                          AddressRepository addressRepository) {
        this.legacyMemberService = legacyMemberService;
        this.cartService = cartService;
        this.messageService = messageService;
        this.cartRepository = cartRepository;
        this.goodsRepository = goodsRepository;
        this.goodsOptionRepository = goodsOptionRepository;
        this.legacyStoreRepository = legacyStoreRepository;
        this.addressRepository = addressRepository;
    }

    @GetMapping("/count")
    public CartCountResponse getCartItemCount() {
        return new CartCountResponse(cartRepository.countByCreatedById(legacyMemberService.currentMemberId()));
    }

    @GetMapping
    public CartService.CartInfo getCartItemList() {
        return cartService.getCartItemList(legacyMemberService.currentMemberId());
    }

    @PostMapping
    public CartService.CartInfo addCart(@Valid @RequestBody AddCartRequest request,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        if (request.getItems().size() + cartRepository.countByCreatedById(legacyMemberService.currentMemberId()) > 100) {
            throw new BadRequestException(messageService.getMessage(CART_TOO_MANY_ITEMS, lang));
        }
        cartService.addItems(request, legacyMemberService.currentMemberId(), lang);
        return cartService.getCartItemList(legacyMemberService.currentMemberId());
    }

    @PatchMapping("/all")
    public CartService.CartInfo updateAllCart(@Valid @RequestBody UpdateCartRequest request) {
        cartService.updateAllItems(request, legacyMemberService.currentMember());
        return cartService.getCartItemList(legacyMemberService.currentMember().getId());
    }

    @PatchMapping("{id}")
    public CartService.CartInfo updateCart(@PathVariable Long id,
                                           @Valid @RequestBody UpdateCartRequest request,
                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        cartService.updateItem(id, legacyMemberService.currentMemberId(), request, lang);
        return cartService.getCartItemList(legacyMemberService.currentMemberId());
    }

    @DeleteMapping("{id}")
    public CartService.CartInfo removeCart(@PathVariable Long id,
                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        cartService.removeItem(id, legacyMemberService.currentMemberId(), lang);
        return cartService.getCartItemList(legacyMemberService.currentMemberId());
    }

    @PostMapping("/now")
    public CartService.CartInfo calculateInstantCartInfo(@Valid @RequestBody AddCartRequest request,
                                                         @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
                                                         @RequestParam(name = "broker", required = false) Long broker) {
        List<Cart> list = new ArrayList<>();
        for (CartItemRequest item : request.getItems()) {
            list.add(getValidCartItem(item.getGoodsNo(), item.getOptionNo(), item.getQuantity(), lang));
        }

        return cartService.getCartItemList(list, TimeSaleCondition.createWithBroker(broker));
    }

    @PostMapping("/now2")
    public CartService.CartInfo calculateInstantCartInfo2(@Valid @RequestBody AddCartRequest request,
                                                          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang,
                                                          @RequestParam(name = "broker", required = false) Long broker) {
        List<Cart> list = new ArrayList<>();
        log.info("{}", request.getItems());

        for (CartItemRequest item : request.getItems()) {
            list.add(getValidCartItem(item.getGoodsNo(), item.getOptionNo(), item.getQuantity(), lang));
        }

        CartService.CartInfo cartInfo = cartService.getCartItemList(list, TimeSaleCondition.createWithBroker(broker));

        // check area shipping
        Member member = legacyMemberService.currentMember();
        Address address = null;
        if (request.addressId != null) {
            address = addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(request.addressId, member.getId())
                    .orElseThrow(() -> new NotFoundException(messageService.getMessage(ADDRESS_NOT_FOUND, lang)));
        } else {
            Optional<Address> baseAddress = addressRepository.findByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(member.getId());
            if (baseAddress.isPresent()) {
                address = baseAddress.get();
            }
        }
        if (address != null) {
            int shippingAmount = cartService.updateShippingAmount(cartInfo, address.getAreaShipping());
            cartInfo.setShippingAmount(shippingAmount);
        }

        return cartInfo;
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

    @Data
    @NoArgsConstructor
    public static class AddCartRequest {
        @Valid
        List<CartItemRequest> items;

        Long addressId;
    }

    @Data
    @NoArgsConstructor
    public static class CartItemRequest {
        @NotNull
        private String goodsNo;

        @NotNull
        private Integer optionNo;

        @NotNull
        @Min(1)
        private Integer quantity;
    }

    @Data
    @NoArgsConstructor
    public static class UpdateCartRequest {
        private Boolean checked;
        private Integer quantity;
    }

    @Data
    @AllArgsConstructor
    private static class CartCountResponse {
        private Integer count;
    }
}