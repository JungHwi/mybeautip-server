package com.jocoos.mybeautip.member.block;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "members_block")
@Data
@NoArgsConstructor
public class Block {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long i;
  private Long you;
  private Long createdAt;
  
  public Block(String i, String you) {
    this.i = Long.parseLong(i);
    this.you = Long.parseLong(you);
    this.createdAt = System.currentTimeMillis();
  }
}