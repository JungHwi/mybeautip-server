package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.revenue.RevenuePayment;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "revenue_payment_detail", types = {RevenuePayment.class})
public interface RevenuePaymentExceprt {

    Long getId();

    Member getMember();

    String getDate();

    Integer getState();

    Integer getEstimatedAmount();

    Integer getFinalAmount();

    String getPaymentMethod();

    Date getPaymentDate();

    Date getCreatedAt();
}


