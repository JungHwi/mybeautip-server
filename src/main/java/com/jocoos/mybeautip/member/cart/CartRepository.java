package com.jocoos.mybeautip.member.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByGoodsGoodsNoAndOptionNo(String goodsNo, Integer optionNo);
  Optional<Cart> findByGoodsGoodsNo(String goodsNo);
  Integer countByCreatedBy(Long createdBy);

  @Query("select c from Cart c where c.createdBy = ?1 order by c.modifiedAt")
  List<Cart> getCartItems(Long createdBy);
}