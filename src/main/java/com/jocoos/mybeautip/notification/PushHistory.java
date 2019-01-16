package com.jocoos.mybeautip.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "push_history")
public class PushHistory extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "target")
  private Member target;
  
  @Column
  private String os;
  
  @Column
  private String resourceType;
  
  @Column
  private String resourceIds;
  
  @Column
  private String title;
  
  @Column(nullable = false)
  private String body;
  
  @Column
  private Boolean success;
  
  public PushHistory(Notification notification, String os, boolean success) {
    this.target = notification.getTargetMember();
    this.os = os;
    this.resourceType = notification.getResourceType();
    this.resourceIds = notification.getResourceIds();
    this.title = notification.getInstantMessageTitle();
    this.body = notification.getInstantMessageBody();
    this.success = success;
  }
}
