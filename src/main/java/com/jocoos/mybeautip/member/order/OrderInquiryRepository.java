package com.jocoos.mybeautip.member.order;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderInquiryRepository extends JpaRepository<OrderInquiry, Long> {

  Slice<OrderInquiry> findByState(Byte state, Pageable pageable);

  Slice<OrderInquiry> findByStateAndCreatedAtBefore(Byte state, Date createdAt, Pageable pageable);

  Slice<OrderInquiry> findByStateGreaterThanEqual(Byte state, Pageable pageable);

  Slice<OrderInquiry> findByStateGreaterThanEqualAndCreatedAtBefore(Byte state, Date createdAt, Pageable pageable);

}
