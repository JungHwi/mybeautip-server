package com.jocoos.mybeautip.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@Entity
@Table(name = "apple_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AppleMember extends CreatedDateAuditable {

  @Id
  @Column
  private String appleId;

  @Column(nullable = false)
  private String email;

  @Column
  private String name;

  @Column(nullable = false)
  private Long memberId;

  public AppleMember(String appleId, String email, String name, Long memberId) {
    this.appleId = appleId;
    this.memberId = memberId;
    if (!Strings.isNullOrEmpty(email)) {
      this.email = email;
    }

    if (!Strings.isNullOrEmpty(name)) {
      this.name = name;
    }
  }
}
