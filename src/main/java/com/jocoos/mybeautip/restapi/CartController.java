package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  private final MemberService memberService;
  private final CartService cartService;
  private final CartRepository cartRepository;
  private final GoodsRepository goodsRepository;
  private final GoodsOptionRepository goodsOptionRepository;
  private final StoreRepository storeRepository;

  public CartController(MemberService memberService,
                        CartService cartService,
                        CartRepository cartRepository,
                        GoodsRepository goodsRepository,
                        GoodsOptionRepository goodsOptionRepository,
                        StoreRepository storeRepository) {
    this.memberService = memberService;
    this.cartService = cartService;
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
  public CartService.CartInfo addCart(@Valid @RequestBody AddCartRequest request) {
    Cart item = getValidCartItem(request.getGoodsNo(), request.getOptionNo(), request.getQuantity());

    Optional<Cart> optionalCart;
    if (request.getOptionNo() == 0) {
      optionalCart = cartRepository.findByGoodsGoodsNoAndCreatedById(request.getGoodsNo(), memberService.currentMemberId());
    } else {
      optionalCart = cartRepository.findByGoodsGoodsNoAndOptionOptionNoAndCreatedById(
        request.getGoodsNo(), request.getOptionNo(), memberService.currentMemberId());
    }

    if (optionalCart.isPresent()) { // Update quantity
      Cart cart = optionalCart.get();
      cart.setQuantity(request.getQuantity());
      cartService.update(cart);
    } else {  // Insert new item
      cartService.save(item);
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
                                         @Valid @RequestBody UpdateCartRequest request) {
    Optional<Cart> optional = cartRepository.findByIdAndCreatedById(id, memberService.currentMemberId());
    Cart cart;

    if (optional.isPresent()) {
      cart = optional.get();
      if (request.getQuantity() != null && request.getQuantity() > 0) {
        cart.setQuantity(request.getQuantity());
      }
      if (request.getChecked() != null) {
        cart.setChecked(request.getChecked());
      }
    } else {
      throw new NotFoundException("cart_item_not_found", "cart item not found, id: " + id);
    }

    cartService.update(cart);
    return cartService.getCartItemList();
  }

  @Transactional
  @DeleteMapping("{id}")
  public CartService.CartInfo removeCart(@PathVariable Long id) {
    Optional<Cart> optional = cartRepository.findByIdAndCreatedById(id, memberService.currentMemberId());
    if (optional.isPresent()) {
      cartRepository.deleteById(id);
    } else {
      throw new NotFoundException("cart_item_not_found", "cart item not found, id: " + id);
    }

    return cartService.getCartItemList();
  }

  @PostMapping("/now")
  public CartService.CartInfo calculateInstantCartInfo(@Valid @RequestBody InstantCartRequest request) {
    List<Cart> list = new ArrayList<>();
    for (AddCartRequest item : request.getItems()) {
      list.add(getValidCartItem(item.getGoodsNo(), item.getOptionNo(), item.getQuantity()));
    }
    return cartService.getCartItemList(list);
  }

  private Cart getValidCartItem(String goodsNo, int optionNo, int quantity) {
    Goods goods = goodsRepository.findById(goodsNo)
      .orElseThrow(() -> new NotFoundException("goods_not_found", "goods not found: " + goodsNo));

    if ("y".equals(goods.getSoldOutFl())) { // 품절 플래그
      throw new BadRequestException("goods_sold_out", "sold out: " + goodsNo);
    }
  
    if ("y".equals(goods.getStockFl()) && goods.getTotalStock() < quantity) { // 재고량에 따름, 총 재고량 추가
      throw new BadRequestException("invalid_quantity", String.format("goodsNo:%s, option:%d, quantity:%d", goodsNo, optionNo, quantity));
    }
  
    if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity < goods.getMinOrderCnt()) { // 최소구매수량 미만
      throw new BadRequestException("invalid_quantity", String.format("goodsNo:%s, option:%d, quantity:%d", goodsNo, optionNo, quantity));
    }
  
    if (goods.getMinOrderCnt() > 0 && goods.getMaxOrderCnt() > 0 && quantity > goods.getMaxOrderCnt()) { // 최대구매수량 초과
      throw new BadRequestException("invalid_quantity", String.format("goodsNo:%s, option:%d, quantity:%d", goodsNo, optionNo, quantity));
    }
    
    GoodsOption option = null;
    if ("y".equals(goods.getOptionFl())) {
      option = goodsOptionRepository.findByGoodsNoAndOptionNo(Integer.parseInt(goodsNo), optionNo)
        .orElseThrow(() -> new NotFoundException("goods_option_not_found", "goods option not found: " + goodsNo));
      if ("y".equals(goods.getStockFl()) && option.getStockCnt() < quantity) { // 재고량에 따름
        throw new BadRequestException("invalid_quantity", String.format("goodsNo:%s, option:%d, quantity:%d", goodsNo, optionNo, quantity));
      }
    } else if (optionNo != 0) {
      throw new BadRequestException("goods_option_not_exist", "goods option not exist:" + goodsNo);
    }

    Store store = storeRepository.findById(goods.getScmNo())
      .orElseThrow(() -> new NotFoundException("store_not_found", "store not found: " + goods.getScmNo()));

    return new Cart(goods, option, store, quantity);
  }

  @Data
  @NoArgsConstructor
  private static class InstantCartRequest {
    List<AddCartRequest> items;
  }

  @Data
  @NoArgsConstructor
  private static class AddCartRequest {
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