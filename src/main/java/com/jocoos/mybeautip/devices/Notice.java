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
@Table(name = "notices")
public class Notice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String os;

  @Column(nullable = false)
  private String message;

  @Column(nullable = false)
  private String minVersion;

  @Column(nullable = false)
  private String maxVersion;

  @Column
  @CreatedBy
  private Long createdBy;

  @Column
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;
}
