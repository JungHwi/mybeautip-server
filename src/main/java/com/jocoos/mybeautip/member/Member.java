package com.jocoos.mybeautip.member;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;


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
  
  static final int LINK_STORE = 8;
  
  public static final int CHAT_POST = 1;
  public static final int COMMENT_POST = 2;
  public static final int LIVE_POST = 4;
  public static final int MOTD_POST = 8;
  public static final int REVENUE_RETURN = 16;

  @Transient
  @JsonIgnore
  private final String defaultAvatarUrl = "https://s3.ap-northeast-2.amazonaws.com/mybeautip/avatar/img_profile_default.png";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @Column(nullable = false)
  private boolean visible;

  @Column(length = 50, nullable = false)
  private String username;

  @Column(length = 200)
  private String avatarUrl;

  @Column(length = 50)
  private String email;
  
  @Column(length = 20)
  private String phoneNumber;
  
  @Column
  private int point;

  @Column(length = 200)
  private String intro;

  @Column(nullable = false)
  private int link;
  
  @Column
  private int permission;
  
  @Column(nullable = false)
  private int followerCount;

  @Column(nullable = false)
  private int followingCount;

  @Column(nullable = false)
  private int reportCount;

  @Column(nullable = false)
  private int publicVideoCount;

  @JsonIgnore
  @Column(nullable = false)
  private int totalVideoCount;

  @Column
  private int revenue;

  @Column
  private Date revenueModifiedAt;
  
  @Column
  private Boolean pushable;

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
    this.username = (StringUtils.isBlank(params.get("username"))) ? "" : params.get("username");
    this.email = (StringUtils.isBlank(params.get("email"))) ? "" : params.get("email");
    this.phoneNumber = (StringUtils.isBlank(params.get("phone_number"))) ? "" : params.get("phone_number");
    this.intro = (StringUtils.isBlank(params.get("intro"))) ? "" : params.get("intro");
    this.avatarUrl = (StringUtils.isBlank(params.get("avatar_url"))) ? defaultAvatarUrl : params.get("avatar_url");
    this.point = 0;
    this.visible = false;
    this.revenueModifiedAt = null;
    this.pushable = true; // default true
    this.permission = (Member.CHAT_POST | Member.COMMENT_POST | Member.LIVE_POST | Member.MOTD_POST | Member.REVENUE_RETURN);
  }
}