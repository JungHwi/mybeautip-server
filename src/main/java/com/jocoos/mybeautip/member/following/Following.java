package com.jocoos.mybeautip.member.following;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member_followings")
public class Following {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "me")
  private Member memberMe;

  @ManyToOne
  @JoinColumn(name = "you")
  private Member memberYou;

  @Column
  @CreatedDate
  public Date createdAt;

  public Following(Member memberMe, Member memberYou) {
    this.memberMe = memberMe;
    this.memberYou = memberYou;
  }
}