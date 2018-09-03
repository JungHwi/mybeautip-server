package com.jocoos.mybeautip.devices;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "devices")
public class Device extends MemberAuditable {

  @Id
  private String id;

  @Column(nullable = false)
  private String arn;

  @Column(nullable = false)
  private String os;

  @Column(nullable = false)
  private String osVersion;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String language;

  @Column(nullable = false)
  private String timezone;

  @Column(nullable = false)
  private String appVersion;

  @Column
  private boolean pushable;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  public Device(String id) {
    this.id = id;
  }

  public void setCreatedBy(Member createdBy) {
    this.createdBy = createdBy;
  }
}
