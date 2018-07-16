package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "members_blocks")
@Data
@NoArgsConstructor
public class Block extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long me;
  private Long you;
  
  public Block(Long me, Long you) {
    this.me = me;
    this.you = you;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
}