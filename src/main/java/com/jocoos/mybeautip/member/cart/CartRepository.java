package com.jocoos.mybeautip.member.cart;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByGoodsGoodsNoAndCreatedById(String goodsNo, Long memberId);

    Optional<Cart> findByGoodsGoodsNoAndOptionOptionNoAndCreatedById(String goodsNo, Integer optionNo, Long memberId);

    Optional<Cart> findByGoodsGoodsNoAndOptionIsNullAndCreatedById(String goodsNo, Long memberId);

    Optional<Cart> findByIdAndCreatedById(Long id, Long memberId);

    Integer countByCreatedById(Long createdBy);

    List<Cart> findAllByCreatedByIdOrderByModifiedAtDesc(Long id);

    List<Cart> findByCreatedById(Long id);

    @Modifying
    @Query("update Cart c set c.checked = ?1 where c.createdBy = ?2")
    void updateAllChecked(Boolean checked, Member me);
}