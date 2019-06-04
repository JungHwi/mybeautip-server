package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import com.jocoos.mybeautip.goods.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.cart.Cart;
import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.cart.CartService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.store.StoreRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/carts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartController {

  private static final String CART_TOO_MANY_ITEMS = "cart.too_many_items";
  private static final String CART_GOODS_SOLD_OUT = "cart.goods_sold_out";
  private static final String CART_OPTION_SOLD_OUT = "cart.option_sold_out";
  private static final String CART_INVALID_QUANTITY = "cart.invalid_quantity";

  private final MemberService memberService;
  private final CartService cartService;
  private final MessageService messageService;
  private final CartRepository cartRepository;
  private final GoodsRepository goodsRepository;
  private final GoodsOptionRepository goodsOptionRepository;
  private final StoreRepository storeRepository;

  private static final String GOODS_NOT_FOUND = "goods.not_found";
  private static final String OPTION_NOT_FOUND = "option.not_found";
  private static final String CART_ITEM_NOT_FOUND = "cart.item_not_found";
  private static final String STORE_NOT_FOUND = "store.not_found";

  public CartController(MemberService memberService,
                        CartService cartService,
                        MessageService messageService,
                        CartRepository cartRepository,
                        GoodsRepository goodsRepository,
                        GoodsOptionRepository goodsOptionRepository,
                        StoreRepository storeRepository) {
    this.memberService = memberService;
    this.cartService = cartService;
    this.messageService = messageService;
    this.cartRepository = cartRepository;
    this.goodsRepository = goodsRepository;
    this.goodsOptionRepository = goodsOptionRepository;
    this.storeRepository = storeRepository;
  }

  @GetMapping("/count")
  public CartCountResponse getCartItemCount() {
    return new CartCountResponse(cartRepository.countByCreatedById(memberService.currentMemberId()));
  }

  @GetMapping
  public CartService.CartInfo getCartItemList() {
    return cartService.getCartItemList(memberService.currentMemberId());
  }
  
  @PostMapping
  public CartService.CartInfo addCart(@Valid @RequestBody AddCartRequest request,
                                      @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    if (request.getItems().size() + cartRepository.countByCreatedById(memberService.currentMemberId()) > 100) {
      throw new BadRequestException("too_many_items", messageService.getMessage(CART_TOO_MANY_ITEMS, lang));
    }
    cartService.addItems(request, memberService.currentMemberId(), lang);
    return cartService.getCartItemList(memberService.currentMemberId());
  }

  @PatchMapping("/all")
  public CartService.CartInfo updateAllCart(@Valid @RequestBody UpdateCartRequest request) {
    return cartService.updateAllItems(request, memberService.currentMember());
  }

  @PatchMapping("{id}")
  public CartService.CartInfo updateCart(@PathVariable Long id,
                                         @Valid @RequestBody UpdateCartRequest request,
                                         @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return cartService.updateItem(id, memberService.currentMemberId
            (), request, lang);
  }

  @DeleteMapping("{id}")
  public CartService.CartInfo removeCart(@PathVariable Long id,
                                         @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return cartService.removeItem(id, memberService.currentMemberId(), lang);
  }

  @PostMapping("/now")
  public CartService.CartInfo calculateInstantCartInfo(@Valid @RequestBody AddCartRequest request,
                                                       @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang,
                                                       @RequestParam(name = "broker", required = false) Long broker) {
    List<Cart> list = new ArrayList<>();
    for (CartItemRequest item : request.getItems()) {
      list.add(getValidCartItem(item.getGoodsNo(), item.getOptionNo(), item.getQuantity(), lang));
    }

    return cartService.getCartItemList(list, TimeSaleCondition.createWithBroker(broker));
  }

  private Cart getValidCartItem(String goodsNo, int optionNo, int quantity, String lang) {
    Goods goods = goodsRepository.findByGoodsNoAndStateLessThanEqual(goodsNo, Goods.GoodsState.NO_SALE.ordinal())
      .orElseThrow(() -> new NotFoundException("goods_not_found", messageService.getMessage(GOODS_NOT_FOUND, lang)));

    if ("y".equals(goods.getSoldOutFl())) { // 품절 플래그
      throw new BadRequestException("goods_sold_out", messageService.getMessage(CART_GOODS_SOLD_OUT, lang));
    }
  
    if ("y".equals(goods.getStockFl()) && quantity > goods.getTotalStock()) { // 재고량에 따름, 총 재고량 추가
      throw new BadRequestException("invalid_quantity", messageService.getMessage(CART_INVALID_QUANTITY, lang));
    }
  
    if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity < goods.getMinOrderCnt()) { // 최소구매수량 미만
      throw new BadRequestException("invalid_quantity", messageService.getMessage(CART_INVALID_QUANTITY, lang));
    }

    if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity > goods.getMaxOrderCnt()) { // 최대구매수량 초과
      throw new BadRequestException("invalid_quantity", messageService.getMessage(CART_INVALID_QUANTITY, lang));
    }
    
    GoodsOption option = null;
    if ("y".equals(goods.getOptionFl())) {
      option = goodsOptionRepository.findByGoodsNoAndOptionNoAndOptionViewFl(Integer.parseInt(goodsNo), optionNo, "y")
        .orElseThrow(() -> new NotFoundException("option_not_found", messageService.getMessage(OPTION_NOT_FOUND, lang)));
      if ("n".equals(option.getOptionSellFl())) { // 옵션 판매안함
        throw new BadRequestException("option_sold_out", messageService.getMessage(CART_OPTION_SOLD_OUT, lang));
      }
      if ("y".equals(goods.getStockFl()) && quantity > option.getStockCnt()) { // 재고량에 따름
        throw new BadRequestException("invalid_quantity", messageService.getMessage(CART_INVALID_QUANTITY, lang));
      }
    } else if (optionNo != 0) {
      throw new NotFoundException("option_not_found", messageService.getMessage(OPTION_NOT_FOUND, lang));
    }

    Store store = storeRepository.findById(goods.getScmNo())
      .orElseThrow(() -> new NotFoundException("store_not_found", messageService.getMessage(STORE_NOT_FOUND, lang)));

    return new Cart(goods, option, store, quantity);
  }

  @Data
  @NoArgsConstructor
  public static class AddCartRequest {
    @Valid
    List<CartItemRequest> items;
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