package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jocoos.mybeautip.notification.MessageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsOption;
import com.jocoos.mybeautip.goods.GoodsOptionRepository;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.cart.Cart;
import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.cart.CartService;
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
    return cartService.getCartItemList();
  }

  @Transactional
  @PostMapping
  public CartService.CartInfo addCart(@Valid @RequestBody AddCartRequest request,
                                      @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    List<Cart> list = new ArrayList<>();
    if (request.getItems().size() + cartRepository.countByCreatedById(memberService.currentMemberId()) > 100) {
      throw new BadRequestException("too_many_items", messageService.getMessage(CART_TOO_MANY_ITEMS, lang));
    }
    
    for (CartItemRequest requestItem : request.getItems()) {
      Cart item = getValidCartItem(requestItem.getGoodsNo(), requestItem.getOptionNo(), requestItem.getQuantity(), lang);
      
      Optional<Cart> optionalCart;
      if (item.getOption() == null) {
        optionalCart = cartRepository.findByGoodsGoodsNoAndCreatedById(item.getGoods().getGoodsNo(), memberService.currentMemberId());
      } else {
        optionalCart = cartRepository.findByGoodsGoodsNoAndOptionOptionNoAndCreatedById(
            item.getGoods().getGoodsNo(), item.getOption().getOptionNo(), memberService.currentMemberId());
      }
  
      if (optionalCart.isPresent()) { // Update quantity
        Cart cart = optionalCart.get();
        cart.setQuantity(item.getQuantity());
        cartService.update(cart);
      } else {  // Insert new item
        cartService.save(item);
      }
    }
    return cartService.getCartItemList();
  }

  @Transactional
  @PatchMapping("/all")
  public CartService.CartInfo updateAllCart(@Valid @RequestBody UpdateCartRequest request) {
    boolean checked = (request.getChecked() == null) ? true : request.getChecked();
    cartRepository.updateAllChecked(checked, memberService.currentMember());

    return cartService.getCartItemList();
  }

  @Transactional
  @PatchMapping("{id}")
  public CartService.CartInfo updateCart(@PathVariable Long id,
                                         @Valid @RequestBody UpdateCartRequest request,
                                         @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Cart item = cartRepository.findByIdAndCreatedById(id, memberService.currentMemberId())
        .map(cart -> {
          if (request.getQuantity() != null && request.getQuantity() > 0) {
            cart.setQuantity(request.getQuantity());
          }
          if (request.getChecked() != null) {
            cart.setChecked(request.getChecked());
          }
          return cart;
        })
        .orElseThrow(() -> new NotFoundException("cart_item_not_found", messageService.getMessage(CART_ITEM_NOT_FOUND, lang)));

    cartService.update(item);
    return cartService.getCartItemList();
  }

  @Transactional
  @DeleteMapping("{id}")
  public CartService.CartInfo removeCart(@PathVariable Long id,
                                         @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Optional<Cart> optional = cartRepository.findByIdAndCreatedById(id, memberService.currentMemberId());
    if (optional.isPresent()) {
      cartRepository.deleteById(id);
    } else {
      throw new NotFoundException("cart_item_not_found", messageService.getMessage(CART_ITEM_NOT_FOUND, lang));
    }
    return cartService.getCartItemList();
  }

  @PostMapping("/now")
  public CartService.CartInfo calculateInstantCartInfo(@Valid @RequestBody AddCartRequest request,
                                                       @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    List<Cart> list = new ArrayList<>();
    for (CartItemRequest item : request.getItems()) {
      list.add(getValidCartItem(item.getGoodsNo(), item.getOptionNo(), item.getQuantity(), lang));
    }
    return cartService.getCartItemList(list);
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
      option = goodsOptionRepository.findByGoodsNoAndOptionNo(Integer.parseInt(goodsNo), optionNo)
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
      .orElseThrow(() -> new NotFoundException("store_not_found", "store not found: " + goods.getScmNo()));

    return new Cart(goods, option, store, quantity);
  }

  @Data
  @NoArgsConstructor
  private static class AddCartRequest {
    List<CartItemRequest> items;
  }

  @Data
  @NoArgsConstructor
  private static class CartItemRequest {
    @NotNull
    private String goodsNo;

    @NotNull
    private int optionNo;

    @NotNull
    private int quantity;
  }

  @Data
  @NoArgsConstructor
  private static class UpdateCartRequest {
    private Boolean checked;
    private Integer quantity;
  }

  @Data
  @AllArgsConstructor
  private static class CartCountResponse {
    private Integer count;
  }
}