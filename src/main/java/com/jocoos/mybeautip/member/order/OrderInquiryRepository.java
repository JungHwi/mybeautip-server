package com.jocoos.mybeautip.member.order;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;

public interface OrderInquiryRepository extends JpaRepository<OrderInquiry, Long> {

  Optional<OrderInquiry> findByOrderAndCreatedBy(Order order, Member member);

  Optional<OrderInquiry> findByIdAndCreatedById(Long id, Long createdBy);

  Slice<OrderInquiry> findByStateAndCreatedById(Byte state, Long createdBy, Pageable pageable);

  Slice<OrderInquiry> findByStateAndCreatedAtBeforeAndCreatedById(Byte state, Date createdAt, Long createdBy, Pageable pageable);

  Slice<OrderInquiry> findByCreatedByIdAndStateGreaterThanEqual(Long createdBy, Byte state, Pageable pageable);

  Slice<OrderInquiry> findByStateGreaterThanEqualAndCreatedAtBeforeAndCreatedById(Byte state, Date createdAt, Long createdBy, Pageable pageable);
}
