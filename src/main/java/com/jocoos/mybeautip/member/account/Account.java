package com.jocoos.mybeautip.member.account;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts")
public class Account {

  @Id
  @CreatedBy
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
  @LastModifiedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

}
