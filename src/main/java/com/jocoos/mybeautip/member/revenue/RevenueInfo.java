package com.jocoos.mybeautip.member.revenue;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import lombok.Data;

import com.jocoos.mybeautip.restapi.OrderController;

@Data
public class RevenueInfo {

  private Long id;
  private Long videoId;
  private OrderController.PurchaseInfo purchaseInfo;
  private int revenue;
  private Date createdAt;

  public RevenueInfo(Revenue revenue) {
    BeanUtils.copyProperties(revenue, this);
    this.videoId = revenue.getVideo().getId();
    this.purchaseInfo = new OrderController.PurchaseInfo(revenue.getPurchase());
  }
}
