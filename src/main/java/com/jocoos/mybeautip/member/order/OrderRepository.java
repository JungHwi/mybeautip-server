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

  Slice<Order> findByCreatedByIdAndStateLessThanEqualAndCreatedAtBefore(Long createdBy, int state, Date createdAt, Pageable pageable);

  Slice<Order> findByCreatedByIdAndCreatedAtBeforeAndStatusContains(Long createdBy, Date createdAt, String status, Pageable pageable);

  List<Order> findByCreatedByIdAndStateLessThanEqualAndCreatedAtBetween(Long createdBy, int state, Date createdAtStart, Date createdAtEnd);
}
