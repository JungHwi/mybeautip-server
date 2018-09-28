package com.jocoos.mybeautip.member.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByGoodsGoodsNoAndOptionNo(String goodsNo, Integer optionNo);

  Optional<Cart> findByIdAndCreatedById(Long id, Long memberId);

  Integer countByCreatedById(Long createdBy);

  List<Cart> findAllByCreatedByIdOrderByModifiedAtDesc(Long id);
}