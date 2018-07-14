package com.jocoos.mybeautip.follow.member;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "members_followings")
@Data
@NoArgsConstructor
public class Follow {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long i;
  private Long you;
  private Long createdAt;
  
  public Follow(String i, String you) {
    this.i = Long.parseLong(i);
    this.you = Long.parseLong(you);
    this.createdAt = System.currentTimeMillis();
  }
}