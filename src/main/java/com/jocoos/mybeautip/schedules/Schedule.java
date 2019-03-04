package com.jocoos.mybeautip.schedules;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

import com.jocoos.mybeautip.audit.MemberAuditable;

@Data
@Entity
@Table(name = "schedules")
public class Schedule extends MemberAuditable  {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String title;

  @Column
  private String thumbnailUrl;

  @Column
  private Date startedAt;

  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;
}
