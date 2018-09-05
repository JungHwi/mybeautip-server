package com.jocoos.mybeautip.member.order;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.audit.ModifiedDateAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "order_inquiries")
public class OrderInquiry extends MemberAuditable {

  public static Byte STATE_CANCEL_ORDER = 0;
  public static Byte STATE_REQUEST_EXCHANGE = 1;
  public static Byte STATE_REQUEST_RETURN = 2;

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

  @LastModifiedDate
  private Date modifiedAt;

  public OrderInquiry(Order order, Byte state, String reason) {
    this.order = order;
    this.state = state;
    this.reason = reason;
  }
}
