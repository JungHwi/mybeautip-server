package com.jocoos.mybeautip.search;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "search_history")
public class SearchHistory extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column
  private String keyword;
  
  @Column(nullable = false)
  private Integer category; // 0: member, 1: video, 2: goods, 3: post
  
  @Column(nullable = false)
  private Boolean isGuest;
  
  public SearchHistory(String keyword, int category, Member me) {
    this.keyword = keyword;
    this.category = category;
    this.isGuest = (me == null);
    this.createdBy = me;
  }
}