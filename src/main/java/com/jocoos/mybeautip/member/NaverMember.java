package com.jocoos.mybeautip.member;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "naver_members")
@Data
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
}
