package com.jocoos.mybeautip.member;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
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
  private int coin;

  @Column(length = 200)
  private String intro;

  @Column(nullable = false)
  private int link;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date updatedAt;

  @Column
  private Date deletedAt;

  public Member(Map<String, String> params) {
    switch (params.get("grant_type")) {
      case "facebook": {
        create(params, LINK_FACEBOOK);
        break;
      }
      case "naver": {
        create(params, LINK_NAVER);
        break;
      }
      case "kakao": {
        create(params, LINK_KAKAO);
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown grant type");
      }
    }
  }

  private Member create(Map<String, String> params, int link) {
    Member member = new Member();
    member.setEmail(params.get("email"));
    member.setUsername(params.get("username"));
    member.setAvatarUrl(params.get("avatar_url"));
    member.setCoin(0);
    member.setLink(link);
    return member;
  }
}
