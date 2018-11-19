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
@Table(name = "kakao_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class KakaoMember extends CreatedDateAuditable {

  @Id
  @Column(nullable = false, length = 30)
  private String kakaoId;

  @Column(nullable = false)
  private Long memberId;

  public KakaoMember(String kakaoId, Long memberId) {
    this.kakaoId = kakaoId;
    this.memberId = memberId;
  }
}
