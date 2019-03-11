package com.jocoos.mybeautip.member.order;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByIdAndDeletedAtIsNull(Long id);

  Optional<Order> findByIdAndCreatedById(Long id, Long createdBy);

  Slice<Order> findByCreatedByIdAndStateLessThanEqualAndCreatedAtBefore(Long createdBy, int state, Date createdAt, Pageable pageable);

  Slice<Order> findByCreatedByIdAndCreatedAtBeforeAndStatusContains(Long createdBy, Date createdAt, String status, Pageable pageable);

  List<Order> findByCreatedByIdAndStateLessThanEqual(Long createdBy, int state);
  
  List<Order> findByCreatedByIdAndStateAndDeliveredAtAfter(Long createdBy, int state, Date weekAgo);

  /**
   * Apis for admin
   */
  Page<Order> findByStateOrderByCreatedAtDesc(int state, Pageable page);

  Page<Order> findByPurchasesGoodsScmNoAndState(int scmNo, int state, Pageable page);

  Page<Order> findByPurchasesGoodsScmNo(int scmNo, Pageable page);

  Page<Order> findByCreatedByIdAndState(Long memberId, int state, Pageable page);

  Page<Order> findByStateLessThanEqual(int state, Pageable page);

  Page<Order> findByStateLessThanEqualAndCreatedById(int state, Long memberId, Pageable page);
  
}
