package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.audit.DateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

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

  @Column(length = 2)
  private String gender;

  @Column(length = 10)
  private int age;

  @Column(length = 10)
  private String birthday;

  @Column(nullable = false)
  private Long memberId;

  public NaverMember(Map<String, String> params, Long memberId) {
    this.naverId = params.get("naver_id");
    this.nickname = params.get("nickname");
    this.gender = params.get("gender");
    this.age = Integer.parseInt(params.get("age"));
    this.birthday = params.get("birthday");
    this.memberId = memberId;
  }
}
