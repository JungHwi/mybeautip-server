package com.jocoos.mybeautip.member;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "kakao_members")
@Data
public class KakaoMember {

  @Id
  @Column(nullable = false, length = 30)
  private String kakaoId;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public KakaoMember(String kakaoId, Long memberId) {
    this.kakaoId = kakaoId;
    this.memberId = memberId;
  }
}
