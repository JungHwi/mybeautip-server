package com.jocoos.mybeautip.member;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "facebook_members")
@Data
public class FacebookMember {

  @Id
  @Column(nullable = false, length = 20)
  private String facebookId;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;
}
