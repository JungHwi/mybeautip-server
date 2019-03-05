package com.jocoos.mybeautip.schedules;

import javax.persistence.*;
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
@Table(name = "schedules")
public class Schedule extends CreatedDateAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String title;

  @Column
  private String thumbnailUrl;

  @ManyToOne
  @JoinColumn(name = "created_by")
  protected Member createdBy;

  @Column
  private Date startedAt;

  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;
}
