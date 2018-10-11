package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
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

  @PostMapping
  public CartService.CartInfo addCart(@Valid @RequestBody AddCartRequest request) {
    Goods goods = goodsRepository.findById(request.getGoodsNo())
      .orElseThrow(() -> new NotFoundException("goods_not_found", "goods not found: " + request.getGoodsNo()));

    if ("y".equals(goods.getSoldOutFl())) {
      throw new BadRequestException("goods_sold_out", "sold out: " + request.getGoodsNo());
    }

    GoodsOption option = null;
    if ("y".equals(goods.getOptionFl())) {
      option = goodsOptionRepository.findByGoodsNoAndOptionNo(Integer.parseInt(goods.getGoodsNo()), request.getOptionNo())
        .orElseThrow(() -> new NotFoundException("goods_option_not_found", "goods option not found: " + request.getGoodsNo()));
      if ("y".equals(goods.getStockFl()) && option.getStockCnt() < request.getQuantity()) { // 재고량에 따름
        throw new BadRequestException("invalid_quantity", "goods option stock count is lower than quantity: " + request.getGoodsNo());
      }
    } else if (request.getOptionNo() != 0) {
      throw new BadRequestException("goods_option_not_exist", "goods option not exist:" + request.getGoodsNo());
    }

    Store store = storeRepository.findById(goods.getScmNo())
      .orElseThrow(() -> new NotFoundException("store_not_found", "store not found: " + goods.getScmNo()));

    Optional<Cart> optionalCart = cartRepository.findByGoodsGoodsNoAndOptionOptionNo(
      request.getGoodsNo(), request.getOptionNo());
    Cart cart;
    if (optionalCart.isPresent()) { // Update quantity
      cart = optionalCart.get();
      cart.setQuantity(request.getQuantity());
    } else {  // Insert new item
      cart = new Cart(goods, option, store, request.getQuantity());
    }
    cartRepository.save(cart);
    return cartService.getCartItemList();
  }

  @PatchMapping("{id}")
  public CartService.CartInfo updateCart(@PathVariable Long id,
                                         @Valid @RequestBody UpdateCartRequest request) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    Optional<Cart> optional = cartRepository.findByIdAndCreatedById(id, memberId);
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

    cart.setModifiedAt(cart.getModifiedAt()); // Not change modifiedAt
    cartRepository.save(cart);
    return cartService.getCartItemList();
  }

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