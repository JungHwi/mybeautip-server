package com.jocoos.mybeautip.member;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Data
@NoArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "members")
public class Member {

  static final int LINK_FACEBOOK = 1;
  static final int LINK_NAVER = 2;
  static final int LINK_KAKAO = 4;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false)
  private String username;

  @Column(length = 200)
  private String avatarUrl;

  @Column(length = 50)
  private String email;

  @Column
  private int point;

  @Column(length = 200)
  private String intro;

  @Column(nullable = false)
  private int link;

  @Column(nullable = false)
  private int followerCount;

  @Column(nullable = false)
  private int followingCount;

  @Column(nullable = false)
  private int videoCount;

  @Column
  private int revenue;

  @Column
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;

  public int parseLink(String grantType) {
    switch (grantType) {
      case "facebook": {
        return LINK_FACEBOOK;
      }
      case "naver": {
        return LINK_NAVER;
      }
      case "kakao": {
        return LINK_KAKAO;
      }
      default: {
        throw new IllegalArgumentException("Unknown grant type");
      }
    }
  }

  public Member(Map<String, String> params) {
    this.link = parseLink(params.get("grant_type"));
    this.username = params.get("username");
    this.email = params.get("email");
    this.avatarUrl = params.get("avatar_url");
    this.point = 0;
  }
}
