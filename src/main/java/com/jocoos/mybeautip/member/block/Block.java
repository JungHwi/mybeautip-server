package com.jocoos.mybeautip.member.block;

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
@Table(name = "member_blocks")
public class Block {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long me;

  @ManyToOne
  @JoinColumn(name = "you")
  private Member memberYou;

  @Column
  @CreatedDate
  public Date createdAt;
  
  public Block(Long me, Member memberYou) {
    this.me = me;
    this.memberYou = memberYou;
  }
}