package com.jocoos.mybeautip.domain.video.persistence.domain;

import com.jocoos.mybeautip.domain.video.code.VideoCategoryType;
import com.jocoos.mybeautip.domain.video.code.VideoMaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "video_category")
public class VideoCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    public Integer id;

    @Column
    public Integer parentId;

    @Enumerated(EnumType.STRING)
    private VideoCategoryType type;

    @Column
    private Integer sort;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String shapeFile;

    @Enumerated(EnumType.STRING)
    private VideoMaskType maskType;
}
