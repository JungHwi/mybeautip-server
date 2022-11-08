package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "video_category_mapping")
public class VideoCategoryMapping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", insertable = false, updatable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private VideoCategory videoCategory;

    public VideoCategoryMapping(Video video, VideoCategory videoCategory) {
        this.video = video;
        this.videoCategory = videoCategory;
    }
}
