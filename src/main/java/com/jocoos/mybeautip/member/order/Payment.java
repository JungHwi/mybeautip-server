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

  public static int STATE_STARTED = 0;
  public static int STATE_STOPPED = 1;
  public static int STATE_FAILED = 2;
  public static int STATE_PURCHASED = 4;
  public static int STATE_PAID = 8;
  public static int STATE_NOTIFIED = 16;
  public static int STATE_CANCELLED = 32;

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  private Order order;

  @Column(nullable = false)
  private String paymentId;

  @Column(nullable = false)
  private Long price;

  /**
   * Payment method. ex) "card"
   */
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

  public Payment(Order order) {
    this.order = order;
    this.state = STATE_STARTED;
  }
}
