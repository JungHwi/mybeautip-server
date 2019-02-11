package com.jocoos.mybeautip.member.revenue;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;

@Slf4j
@Service
public class RevenuePaymentService {
  
  public static final int NOT_PAID = 0;
  public static final int PAID = 1;
  public static final int NOT_AVAILABLE = 2;
  
  private final RevenuePaymentRepository revenuePaymentRepository;
  
  public RevenuePaymentService(RevenuePaymentRepository revenuePaymentRepository) {
    this.revenuePaymentRepository = revenuePaymentRepository;
  }
  
  @Transactional
  public void appendEstimatedAmount(Member member, Date purchaseDate, int price) {
    String targetDate = LocalDate.from(purchaseDate.toInstant().atZone(ZoneId.systemDefault())).toString();
    Optional<RevenuePayment> optional = revenuePaymentRepository.findByMemberAndTargetDate(member, targetDate);
    RevenuePayment revenuePayment;
    if (optional.isPresent()) {
      revenuePayment = optional.get();
      if (revenuePayment.getState() != NOT_PAID) {
        throw new MybeautipRuntimeException("invalid_state", "Invalid Revenue payment state: " + revenuePayment.getState());
      }
      revenuePayment.setEstimatedAmount(revenuePayment.getEstimatedAmount() + price);
    } else {
      revenuePayment = new RevenuePayment(member, targetDate, price);
    }
    revenuePaymentRepository.save(revenuePayment);
  }
  
  @Transactional
  public void addPayment(Member member, String paymentDate, String paymentMethod, int price) {
    String targetDate = getTargetDate(paymentDate);
    Optional<RevenuePayment> optional = revenuePaymentRepository.findByMemberAndTargetDate(member, targetDate);
    
    if (optional.isPresent()) {
      RevenuePayment revenuePayment = optional.get();
      if (!revenuePayment.getEstimatedAmount().equals(price)) {
        throw new MybeautipRuntimeException("price is not match");
      }
      revenuePayment.setState(PAID);
      revenuePayment.setEstimatedAmount(0);
      revenuePayment.setFinalAmount(price);
      revenuePayment.setPaymentMethod(paymentMethod);
      revenuePayment.setPaymentDate(paymentDate);
      revenuePaymentRepository.save(revenuePayment);
    } else {
      throw new NotFoundException("revenue_returns_not_found", "Revenue returns not found");
    }
  }
  
  private String getTargetDate(String date) {
    if (StringUtils.isEmpty(date) || date.length() != 8) {
      throw new MybeautipRuntimeException("invalid_date", "Valid date format is YYYYMMDD");
    }
    return StringUtils.substring(date, 0, 5);
  }
}
