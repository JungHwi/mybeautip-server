package com.jocoos.mybeautip.member;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "naver_members")
@Data
@NoArgsConstructor
public class NaverMember {

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

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public NaverMember(Map<String, String> params, Long memberId) {
    this.naverId = params.get("naver_id");
    this.nickname = params.get("nickname");
    this.gender = params.get("gender");
    this.age = Integer.parseInt(params.get("age"));
    this.birthday = params.get("birthday");
    this.memberId = memberId;
  }
}
