package com.jocoos.mybeautip.member.order;

import javax.persistence.*;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "order_payments")
public class Payment extends ModifiedDateAuditable {

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  private Order order;

  @Column(nullable = false)
  private String paymentId;


  @Column(nullable = false)
  private String method;

  /**
   * 0: created, 1: stopped, 2:failed
   */
  @Column(nullable = false)
  private int state;

  @Column
  private String message;

  @Column
  private String receipt;

  @Column
  private Date deletedAt;

}
