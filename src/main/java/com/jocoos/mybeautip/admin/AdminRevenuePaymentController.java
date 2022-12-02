package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.revenue.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.jocoos.mybeautip.member.revenue.RevenuePaymentService.NOT_PAID;

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
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVENUE_PAYMENT_NOT_FOUND, "Revenue payment not found: " + id));

        if (revenuePayment.getState() != NOT_PAID) {
            throw new BadRequestException(ErrorCode.INVALID_STATE, "Invalid Revenue Payment state: " + revenuePayment.getState());
        }

        Date paymentDate = Dates.parse(request.getPaymentDate());
        RevenuePaymentInfo info = new RevenuePaymentInfo(revenuePaymentService.pay(
                revenuePayment, request.getPaymentMethod(), paymentDate, request.getAmount()));
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
