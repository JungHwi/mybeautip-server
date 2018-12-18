package com.jocoos.mybeautip.tag;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tag_history")
public class TagHistory extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column
  private String tag;
  
  @Column(nullable = false)
  private Integer category; // 0: member, 1: video, 2: goods, 3: post
  
  @Column(nullable = false)
  private Boolean isGuest;
  
  @Column(nullable = false)
  private Integer count;
  
  @Column
  @LastModifiedDate
  private Date modifiedAt;
  
  public TagHistory(String tag, int category, Member me) {
    this.tag = tag;
    this.category = category;
    this.isGuest = (me == null);
    this.createdBy = me;
    this.count = 1;
  }
}
