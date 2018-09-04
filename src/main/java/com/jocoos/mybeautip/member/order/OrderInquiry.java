package com.jocoos.mybeautip.member.order;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;

@NoArgsConstructor
@Data
@Entity
@Table(name = "order_inquiries")
public class OrderInquiry extends ModifiedDateAuditable {

  public static int STATE_CANCEL_ORDER = 0;
  public static int STATE_REQUEST_EXCHANGE = 1;
  public static int STATE_REQUEST_RETURN = 2;

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  private Order order;

  @Column(nullable = false)
  private Byte state;

  @Column(length = 500)
  private String reason;

  @Column(length = 500)
  private String comment;

  @Column
  private boolean completed;

  public OrderInquiry(Order order, Byte state, String reason) {
    this.order = order;
    this.state = state;
    this.reason = reason;
  }
}
