package com.jocoos.mybeautip.admin;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "admin_members")
public class AdminMember {

  @Id
  private String adminId;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;
}
