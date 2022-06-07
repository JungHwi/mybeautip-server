package com.jocoos.mybeautip.member.order;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface OrderInquiryRepository extends JpaRepository<OrderInquiry, Long> {

    Optional<OrderInquiry> findByOrderAndCreatedBy(Order order, Member member);

    Optional<OrderInquiry> findByIdAndCreatedById(Long id, Long createdBy);

    Slice<OrderInquiry> findByStateAndCreatedById(Byte state, Long createdBy, Pageable pageable);

    Slice<OrderInquiry> findByStateAndCreatedAtBeforeAndCreatedById(Byte state, Date createdAt, Long createdBy, Pageable pageable);

    Slice<OrderInquiry> findByCreatedByIdAndStateGreaterThanEqual(Long createdBy, Byte state, Pageable pageable);

    Slice<OrderInquiry> findByStateGreaterThanEqualAndCreatedAtBeforeAndCreatedById(Byte state, Date createdAt, Long createdBy, Pageable pageable);

    Optional<OrderInquiry> findByPurchaseId(Long purchaseId);


    Page<OrderInquiry> findByState(Byte state, Pageable page);

    Page<OrderInquiry> findByStateAndCompletedIsTrue(Byte state, Pageable page);

    Page<OrderInquiry> findByStateAndCompletedIsFalse(Byte state, Pageable page);

    Page<OrderInquiry> findByStateAndPurchaseGoodsScmNo(Byte state, int scmNo, Pageable page);

    Page<OrderInquiry> findByStateAndCompletedIsFalseAndPurchaseGoodsScmNo(Byte state, int scmNo, Pageable page);

    Page<OrderInquiry> findByStateAndCompletedIsTrueAndPurchaseGoodsScmNo(Byte state, int scmNo, Pageable page);

    Page<OrderInquiry> findByStateAndOrderPurchasesGoodsScmNo(Byte state, int scmNo, Pageable page);

    Page<OrderInquiry> findByStateAndCompletedIsFalseAndOrderPurchasesGoodsScmNo(Byte state, int scmNo, Pageable page);

    Page<OrderInquiry> findByStateAndCompletedIsTrueAndOrderPurchasesGoodsScmNo(Byte state, int scmNo, Pageable page);

}
