package com.jocoos.mybeautip.member.order;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import org.aspectj.weaver.ast.Or;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByIdAndCreatedById(Long id, Long createdBy);

  Slice<Order> findByCreatedByIdAndCreatedAtBefore(Long createdBy, Date createdAt, Pageable pageable);
}
