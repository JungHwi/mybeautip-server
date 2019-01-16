package com.jocoos.mybeautip.notification.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.admin.AdminNotificationController;
import com.jocoos.mybeautip.audit.MemberAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "push_messages")
public class PushMessage extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column
  private Integer category;   // 1: instant, ...

  @Column
  private Integer platform; // 0: all, 1: ios, 2: android
  
  @Column
  private String resourceType;
  
  @Column
  private String resourceIds;
  
  @Column
  private String title;
  
  @Column(nullable = false)
  private String body;
  
  @Column
  private Integer targetDeviceCount;
  
  public PushMessage(AdminNotificationController.NotificationRequest request, int count) {
    BeanUtils.copyProperties(request, this);
    this.body = request.getMessage();
    this.targetDeviceCount = count;
  }
}
