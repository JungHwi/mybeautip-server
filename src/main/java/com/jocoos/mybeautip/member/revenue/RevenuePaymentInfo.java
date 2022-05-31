package com.jocoos.mybeautip.member.revenue;

import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class RevenuePaymentInfo {
    private Long id;
    private String date;  // YYYY-MM
    private Integer state;  // 0: not paid, 1: paid, 2: n/a
    private Integer estimatedAmount;
    private Integer finalAmount;
    private String paymentMethod;
    private String paymentDate; // YYYY-MM-DD

    public RevenuePaymentInfo(RevenuePayment revenuePayment) {
        BeanUtils.copyProperties(revenuePayment, this);
    }
}
