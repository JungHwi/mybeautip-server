package com.jocoos.mybeautip.video;

import javax.persistence.*;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "video_categories")
public class VideoCategory implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long videoId;

  @Column(nullable = false)
  private int category;

  public VideoCategory(Long videoId, int category) {
    this.videoId = videoId;
    this.category = category;
  }
}
