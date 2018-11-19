package com.jocoos.mybeautip.member.account;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts")
public class Account {

  @Id
  @Column(name = "member_id")
  private Long memberId;

  @Column
  private String email;

  @Column
  private String bankName;

  @Column
  private String bankAccount;

  @Column
  private String bankDepositor;

  @Column
  private boolean validity;

  @Column
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  public Account(Long memberId) {
    this.memberId = memberId;
  }
}
