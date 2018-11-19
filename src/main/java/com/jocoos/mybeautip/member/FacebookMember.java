package com.jocoos.mybeautip.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@Entity
@Table(name = "facebook_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FacebookMember extends CreatedDateAuditable {

  @Id
  @Column(nullable = false, length = 20)
  private String facebookId;

  @Column(nullable = false)
  private Long memberId;

  public FacebookMember(String facebookId, Long memberId) {
    this.facebookId = facebookId;
    this.memberId = memberId;
  }
}
