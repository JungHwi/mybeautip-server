package com.jocoos.mybeautip.admin;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.store.Store;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "admin_members")
public class AdminMember {

  @Id
  private String email;

  @Column(nullable = false)
  private String password;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "store_id")
  private Store store;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;
}
