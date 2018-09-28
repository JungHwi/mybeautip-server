package com.jocoos.mybeautip.member.revenue;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class RevenueInfo {

  private Date createdAt;

  public RevenueInfo(Revenue revenue) {
    BeanUtils.copyProperties(revenue, this);
  }
}
