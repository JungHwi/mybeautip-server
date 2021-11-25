package com.jocoos.mybeautip.video.scrap;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.video.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "video_scraps")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VideoScrap extends MemberAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "video_id")
  private Video video;

  public VideoScrap(Video video) { this.video = video; }
}
