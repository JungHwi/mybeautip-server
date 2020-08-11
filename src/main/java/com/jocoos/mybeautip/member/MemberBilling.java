package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_billings")
public class MemberBilling extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Boolean base;

  @Column(nullable = false)
  private String customerId;

  @Column(nullable = false)
  private String salt;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  private Boolean valid;

  @Column(nullable = false)
  private String cardName;

  @Column(nullable = false)
  private String cardNumber;

  @Column
  @CreatedDate
  private Date createdAt;
}
