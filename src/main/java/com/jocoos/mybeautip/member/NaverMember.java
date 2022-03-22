package com.jocoos.mybeautip.member;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Map;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@Entity
@Table(name = "naver_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NaverMember extends CreatedDateAuditable {

  @Id
  @Column(nullable = false, length = 30)
  private String naverId;

  @Column(length = 30)
  private String nickname;

  @Column(length = 1)
  private String gender;

  @Column(length = 10)
  private String age;

  @Column(length = 10)
  private String birthday;

  @Column(nullable = false)
  private Long memberId;

  public NaverMember(String naverId, String nickname, Long memberId) {
    this.naverId = naverId;
    this.nickname = nickname;
    this.memberId = memberId;
  }

  public NaverMember(Map<String, String> params, Long memberId) {
    this.naverId = params.get("naver_id");
    this.nickname = params.get("nickname");
    this.gender = params.get("gender");
    this.age = params.get("age");
    this.birthday = params.get("birthday");
    this.memberId = memberId;
  }
}
