package com.jocoos.mybeautip.member.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.MemberService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/carts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final CartRepository cartRepository;
  private final GoodsRepository goodsRepository;

  public CartController(MemberService memberService,
                        GoodsService goodsService,
                        CartRepository cartRepository,
                        GoodsRepository goodsRepository) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.cartRepository = cartRepository;
    this.goodsRepository = goodsRepository;
  }

  @PostMapping
  public CartResponse addCart(AddCartRequest request) {
    Optional<Goods> optional = goodsRepository.findById(request.getGoodsNo());
    Goods goods;

    if (optional.isPresent()) {
      goods = optional.get();
    } else {
      throw new NotFoundException("goods_not_found", "goods not found: " + request.getGoodsNo());
    }

    Cart cart;
    Optional<Cart> optionalCart = cartRepository.findByGoodsGoodsNoAndOptionNo(request.getGoodsNo(), request.getOptionNo());
    if (optionalCart.isPresent()) { // Update quantity
      cart = optionalCart.get();
      cart.setQuantity(request.getQuantity());
    } else {  // Insert new item
      cart = new Cart(goods, request.getOptionNo(), request.getQuantity());
    }
    Cart result = cartRepository.save(cart);
    return new CartResponse(result.getId());
  }

  @PatchMapping("{id}")
  public CartResponse updateCart(@PathVariable Long id,
                                 UpdateCartRequest request) {
    Optional<Cart> optional = cartRepository.findById(id);
    Cart cart;

    if (optional.isPresent()) {
      cart = optional.get();
    } else {
      throw new NotFoundException("cart_item_not_found", "cart item not found, id: " + id);
    }
    Cart result = cartRepository.save(cart);
    return new CartResponse(result.getId());
  }

  @DeleteMapping("{id}")
  public void removeCart(@PathVariable Long id) {
    Optional<Cart> optional = cartRepository.findById(id);

    if (optional.isPresent()) {
      cartRepository.deleteById(id);
    } else {
      throw new NotFoundException("cart_item_not_found", "cart item not found, id: " + id);
    }
  }

  @GetMapping("/count")
  public CartCountResponse getCartItemCount() {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }
    return new CartCountResponse(cartRepository.countByCreatedBy(memberId));
  }

  @Data
  class AddCartRequest {
    String goodsNo;
    Integer optionNo;
    Integer quantity;
  }

  @Data
  class UpdateCartRequest {
    Integer quantity;
  }

  @Data
  @AllArgsConstructor
  class CartResponse {
    Long id;
  }

  @Data
  @AllArgsConstructor
  class CartCountResponse {
    Integer count;
  }

  @Data
  class CartItemGroup {
    Integer scmNo;
    List<CartInfo> content;

    public CartItemGroup(Integer scmNo, List<CartInfo> content) {
      this.scmNo = scmNo;
      this.content = content;
    }
  }

  @Data
  class CartInfo {
    Long id;
    GoodsInfo goods;
    Integer optionNo;
    Integer quantity;

    public CartInfo(Cart cart, GoodsInfo goods) {
      BeanUtils.copyProperties(cart, this);
      this.goods = goods;
    }
  }
}
