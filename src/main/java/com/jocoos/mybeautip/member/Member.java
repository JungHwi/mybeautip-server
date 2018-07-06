package com.jocoos.mybeautip.member;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "members")
@Data
public class Member implements Serializable {

  static final int LINK_FACEBOOK = 1;
  static final int LINK_NAVER = 2;
  static final int LINK_KAKAO = 4;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(length = 50, nullable = false)
  private String username;

  @Column(length = 200)
  private String avatarUrl;

  @Column(length = 50)
  private String email;

  @Column
  private int coin = 0;

  @Column(length = 200)
  private String intro;

  @Column(nullable = false)
  private int link = 0;

  @Column(nullable = false)
  @CreatedDate
  public Date createdAt;

  @Column
  @LastModifiedDate
  public Date updatedAt;

  @Column
  public Date deletedAt;

}
