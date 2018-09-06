package com.jocoos.mybeautip.member.order;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByIdAndDeletedAtIsNull(Long id);

  Optional<Order> findByIdAndCreatedById(Long id, Long createdBy);

  Slice<Order> findByCreatedByIdAndCreatedAtBefore(Long createdBy, Date createdAt, Pageable pageable);

  Slice<Order> findByCreatedByIdAndCreatedAtBeforeAndStatusContains(Long createdBy, Date createdAt, String status, Pageable pageable);
}
