package com.jocoos.mybeautip.member;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "facebook_members")
@Data
@NoArgsConstructor
public class FacebookMember {

  @Id
  @Column(nullable = false, length = 20)
  private String facebookId;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public FacebookMember(String facebookId, Long memberId) {
    this.facebookId = facebookId;
    this.memberId = memberId;
  }
}
