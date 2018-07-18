package com.jocoos.mybeautip.devices;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "devices")
public class Device {

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
  @CreatedBy
  private Long createdBy;

  @Column
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  public Device(String id) {
    this.id = id;
  }
}
