package com.jocoos.mybeautip.member.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByIdAndPaymentId(Long orderId, String paymentId);
}
