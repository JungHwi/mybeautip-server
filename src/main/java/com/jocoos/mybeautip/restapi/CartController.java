package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.cart.Cart;
import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.store.StoreRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/carts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final CartRepository cartRepository;
  private final GoodsRepository goodsRepository;
  private final StoreRepository storeRepository;

  public CartController(MemberService memberService,
                        GoodsService goodsService,
                        CartRepository cartRepository,
                        GoodsRepository goodsRepository,
                        StoreRepository storeRepository) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.cartRepository = cartRepository;
    this.goodsRepository = goodsRepository;
    this.storeRepository = storeRepository;
  }

  @PostMapping
  public ResponseEntity<CartInfo> addCart(@Valid @RequestBody AddCartRequest request) {
    Optional<Goods> optional = goodsRepository.findById(request.getGoodsNo());
    Goods goods;

    if (optional.isPresent()) {
      goods = optional.get();
    } else {
      throw new NotFoundException("goods_not_found", "goods not found: " + request.getGoodsNo());
    }

    Optional<Cart> optionalCart = cartRepository.findByGoodsGoodsNoAndOptionNo(
      request.getGoodsNo(), request.getOptionNo());
    Cart cart;
    if (optionalCart.isPresent()) { // Update quantity
      cart = optionalCart.get();
      cart.setQuantity(request.getQuantity());
    } else {  // Insert new item
      cart = new Cart(goods, request.getOptionNo(), request.getQuantity());
    }
    Cart result = cartRepository.save(cart);
    return new ResponseEntity<>(
      new CartInfo(result, goodsService.generateGoodsInfo(result.getGoods())), HttpStatus.OK);
  }

  @PatchMapping("{id}")
  public ResponseEntity<CartInfo> updateCart(@PathVariable Long id,
                         @Valid @RequestBody UpdateCartRequest request) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    Optional<Cart> optional = cartRepository.findByIdAndCreatedBy(id, memberId);
    Cart cart;

    if (optional.isPresent()) {
      cart = optional.get();
      cart.setQuantity(request.getQuantity());
    } else {
      throw new NotFoundException("cart_item_not_found", "cart item not found, id: " + id);
    }
    Cart result = cartRepository.save(cart);
    return new ResponseEntity<>(
      new CartInfo(result, goodsService.generateGoodsInfo(result.getGoods())), HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public void removeCart(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    Optional<Cart> optional = cartRepository.findByIdAndCreatedBy(id, memberId);

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

  @GetMapping
  public List<CartItemGroup> getCartItems() {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    List<Cart> allCartItems = cartRepository.getCartItems(memberId);

    Map<Integer, List<CartInfo>> map = new HashMap<>();
    List<Integer> orderedScmNo = new ArrayList<>();
    List<CartInfo> list;

    for (Cart cart : allCartItems) {
      if (map.containsKey(cart.getScmNo())) {
        list = map.get(cart.getScmNo());
      } else {
        list = new ArrayList<>();
      }
      list.add(new CartInfo(cart, goodsService.generateGoodsInfo(cart.getGoods())));
      map.put(cart.getScmNo(), list);
      if (!orderedScmNo.contains(cart.getScmNo())) {
        orderedScmNo.add(cart.getScmNo());
      }
    }

    List<CartItemGroup> result = new ArrayList<>();
    for (int scmNo : orderedScmNo) {
      result.add(new CartItemGroup(scmNo, storeRepository.getOne(scmNo).getName(), map.get(scmNo)));
    }

    return result;
  }

  @Data
  @NoArgsConstructor
  private static class AddCartRequest {
    @NotNull
    private String goodsNo;

    @NotNull
    private int optionNo;

    @NotNull
    @Max(100)
    private int quantity;
  }

  @Data
  @NoArgsConstructor
  private static class UpdateCartRequest {
    @NotNull
    @Max(100)
    private int quantity;
  }

  @Data
  @AllArgsConstructor
  private static class CartCountResponse {
    private Integer count;
  }

  @Data
  private static class CartItemGroup {
    private Integer scmNo;
    private String storeName;
    private List<CartInfo> content;

    CartItemGroup(Integer scmNo, String storeName, List<CartInfo> content) {
      this.scmNo = scmNo;
      this.storeName = storeName;
      this.content = content;
    }
  }

  @Data
  private static class CartInfo {
    private Long id;
    private GoodsInfo goods;
    private Integer optionNo;
    private Integer quantity;

    CartInfo(Cart cart, GoodsInfo goods) {
      BeanUtils.copyProperties(cart, this);
      this.goods = goods;
    }
  }
}