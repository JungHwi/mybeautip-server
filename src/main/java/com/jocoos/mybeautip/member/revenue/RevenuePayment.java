package com.jocoos.mybeautip.member.revenue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "revenue_payments")
public class RevenuePayment extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;
  
  @Column
  private String date;  // YYYY-MM
  
  @Column
  private Integer state;  // 0: not paid, 1: paid, 2: n/a
  
  @Column
  private Integer estimatedAmount;
  
  @Column
  private Integer finalAmount;
  
  @Column
  private String paymentMethod;
  
  @Column
  private Date paymentDate;
  
  public RevenuePayment(Member member, String date, int estimatedAmount) {
    this.member = member;
    this.date = date;
    this.estimatedAmount = estimatedAmount;
    this.state = RevenuePaymentService.NOT_PAID;
  }
}
