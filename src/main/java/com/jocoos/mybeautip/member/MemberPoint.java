package com.jocoos.mybeautip.member;

import javax.persistence.*;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_points")
public class MemberPoint extends CreatedDateAuditable {

  public static final int CATEGORY_GET_POINT = 0;
  public static final int CATEGORY_USE_POINT = 1;
  public static final int CATEGORY_EXPIRED_POINT = 2;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 0: Get points, 1: Use points, 2: Points expired
   */
  @Column(nullable = false)
  private int category;

  @Column(length = 30)
  private String usage;

  @Column(nullable = false)
  private int point;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;


  @Column
  private Date earnedAt;

}
