package com.jocoos.mybeautip.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tag_history")
public class TagHistory extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "tag_id")
  private Tag tag;
  
  @Column(nullable = false)
  private Integer category; // 1: member, 2: video, 3: post, 4: banner, 5: comment
  
  @Column(nullable = false)
  private Long resourceId;
  
  @Column(nullable = false)
  private Boolean isGuest;
  
  @Column
  @LastModifiedDate
  private Date modifiedAt;
  
  @ManyToOne
  @JoinColumn(name = "created_by")
  private Member createdBy;
  
  public TagHistory(Tag tag, int category, long resourceId, Member me) {
    this.tag = tag;
    this.category = category;
    this.resourceId = resourceId;
    this.isGuest = (me == null);
    this.createdBy = me;
  }
}
