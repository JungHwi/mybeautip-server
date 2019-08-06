package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.revenue.RevenuePayment;

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


