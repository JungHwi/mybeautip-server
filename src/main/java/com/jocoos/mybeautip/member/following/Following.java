package com.jocoos.mybeautip.member.following;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;

@Entity
@Table(name = "members_followings")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Following extends CreatedDateAuditable {
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

  Following(Member memberMe, Member memberYou) {
    this.memberMe = memberMe;
    this.memberYou = memberYou;
  }
}