package com.jocoos.mybeautip.member.order;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByIdAndDeletedAtIsNull(Long id);

  Optional<Order> findByIdAndCreatedById(Long id, Long createdBy);

  Slice<Order> findByCreatedByIdAndCreatedAtBefore(Long createdBy, Date createdAt, Pageable pageable);

  Slice<Order> findByCreatedByIdAndCreatedAtBeforeAndStatusContains(Long createdBy, Date createdAt, String status, Pageable pageable);

  List<Order> findByCreatedByIdAndCreatedAtBetween(Long createdBy, Date createdAtStart, Date createdAtEnd);

  @Modifying
  @Query("update Purchase p set p.status = ?2 where p.id = ?1")
  void updatePurchaseStatus(Long id, String status);
}
