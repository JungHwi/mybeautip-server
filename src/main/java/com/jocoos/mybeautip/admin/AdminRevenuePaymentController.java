package com.jocoos.mybeautip.admin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.jocoos.mybeautip.member.revenue.RevenuePaymentService.NOT_PAID;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.revenue.RevenuePayment;
import com.jocoos.mybeautip.member.revenue.RevenuePaymentInfo;
import com.jocoos.mybeautip.member.revenue.RevenuePaymentRepository;
import com.jocoos.mybeautip.member.revenue.RevenuePaymentService;
import com.jocoos.mybeautip.member.revenue.RevenueService;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/revenue-payments")
public class AdminRevenuePaymentController {
  
  private final RevenueService revenueService;
  private final RevenuePaymentService revenuePaymentService;
  private final RevenuePaymentRepository revenuePaymentRepository;
  
  public AdminRevenuePaymentController(RevenueService revenueService,
                                       RevenuePaymentService revenuePaymentService,
                                       RevenuePaymentRepository revenuePaymentRepository) {
    this.revenueService = revenueService;
    this.revenuePaymentService = revenuePaymentService;
    this.revenuePaymentRepository = revenuePaymentRepository;
  }
  
  @PatchMapping("/{id:.+}/pay")
  public ResponseEntity<RevenuePaymentInfo> payMonthlyRevenue(@PathVariable Long id,
                                                              @Valid @RequestBody PayMonthlyReveneRequest request) {
    log.debug("request: {}, {}", id, request);
    
    RevenuePayment revenuePayment = revenuePaymentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("revenue_payment_not_found", "Revenue payment not found: " + id));
    
    if (revenuePayment.getState() != NOT_PAID) {
      throw new BadRequestException("invalid_state", "Invalid Revenue Payment state: " + revenuePayment.getState());
    }
    
    RevenuePaymentInfo info = new RevenuePaymentInfo(revenuePaymentService.pay(
        revenuePayment, request.getPaymentMethod(), request.getPaymentDate(), request.getAmount()));
    return new ResponseEntity<>(info, HttpStatus.OK);
  }
  
  @Data
  public static class PayMonthlyReveneRequest {
    @NotNull
    String paymentMethod; // "BankName-AccountNo-Owner"
    
    @NotNull
    String paymentDate; // "YYYY-MM-DD"
    
    @NotNull
    Integer amount;
  }
}