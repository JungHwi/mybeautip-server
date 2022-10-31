package com.jocoos.mybeautip.domain.video.persistence.domain;

import com.jocoos.mybeautip.domain.video.code.VideoCategoryType;
import com.jocoos.mybeautip.domain.video.code.VideoMaskType;
import com.jocoos.mybeautip.video.VideoCategoryMapping;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
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

    @OneToMany(mappedBy = "videoCategory", cascade = CascadeType.ALL)
    private List<VideoCategoryMapping> categoryMapping;
}
