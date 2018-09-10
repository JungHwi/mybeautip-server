package com.jocoos.mybeautip.video.view;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_views")
public class VideoView extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "video_id")
  private Video video;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  public VideoView(Video video, Member member) {
    this.video = video;
    this.createdBy = member;
  }
}