package com.jocoos.mybeautip.member.following;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "members_followings")
@Data
@NoArgsConstructor
public class Following extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long me;
  private Long you;
  
  Following(Long me, Long you) {
    this.me = me;
    this.you = you;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
}