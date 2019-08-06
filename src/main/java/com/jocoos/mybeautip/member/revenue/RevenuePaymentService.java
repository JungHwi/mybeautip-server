package com.jocoos.mybeautip.member.revenue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;

@Slf4j
@Service
public class RevenuePaymentService {
  
  public static final int NOT_PAID = 0;
  public static final int PAID = 1;
  public static final int NOT_AVAILABLE = 2;
  
  private final RevenuePaymentRepository revenuePaymentRepository;
  private final MemberRepository memberRepository;

  public RevenuePaymentService(RevenuePaymentRepository revenuePaymentRepository,
                               MemberRepository memberRepository) {
    this.revenuePaymentRepository = revenuePaymentRepository;
    this.memberRepository = memberRepository;
  }
  
  @Transactional
  public RevenuePayment getRevenuePayment(Member member, Date purchaseDate) {
    String date = generateDate(purchaseDate);
    return revenuePaymentRepository.findByMemberAndDate(member, date)
        .orElseGet(() -> revenuePaymentRepository.save(new RevenuePayment(member, date, 0)));
  }
  
  @Transactional
  public RevenuePayment appendEstimatedAmount(RevenuePayment revenuePayment, int revenuePrice) {
    if (revenuePayment.getState() != NOT_PAID) {
      throw new MybeautipRuntimeException("invalid_revenue_payments_state", "Invalid Revenue payment state: " + revenuePayment.getState());
    }
    revenuePayment.setEstimatedAmount(revenuePayment.getEstimatedAmount() + revenuePrice);
    return revenuePaymentRepository.save(revenuePayment);
  }
  
  @Transactional
  public RevenuePayment pay(RevenuePayment revenuePayment, String paymentMethod, Date paymentDate, int finalAmount) {
    if (revenuePayment.getEstimatedAmount() != finalAmount) {
      throw new MybeautipRuntimeException(String.format("price not match! estimated: %d final: %d",
          revenuePayment.getEstimatedAmount(), finalAmount));
    }

    Member member = revenuePayment.getMember();
    log.info("Member revenue is set subtract {} from {}", finalAmount, member.getRevenue());

    memberRepository.updateRevenue(member.getId(), -(finalAmount));

    revenuePayment.setState(PAID);
    revenuePayment.setEstimatedAmount(0);
    revenuePayment.setFinalAmount(finalAmount);
    revenuePayment.setPaymentMethod(paymentMethod);
    revenuePayment.setPaymentDate(paymentDate);

    return revenuePaymentRepository.save(revenuePayment);
  }
  
  private String generateDate(Date date) {
    LocalDate localDate = LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault()));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM");
    return localDate.format(formatter);
  }
}
