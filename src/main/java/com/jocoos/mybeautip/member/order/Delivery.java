package com.jocoos.mybeautip.member.order;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "order_deliveries")
public class Delivery extends CreatedDateAuditable {

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  private Order order;

  @Column(nullable = false)
  private String recipient;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String zipNo;

  @Column(nullable = false)
  private String roadAddrPart1;

  @Column(nullable = false)
  private String roadAddrPart2;

  @Column(nullable = false)
  private String jibunAddr;

  @Column(nullable = false)
  private String detailAddress;

  @Column
  private String carrier;

  @Column
  private String invoice;

  @Column
  private String carrierMessage;

  public Delivery(Order order) {
    this.order = order;
  }
}
