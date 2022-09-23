package com.jocoos.mybeautip.video;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "video_category_mapping")
public class VideoCategoryMapping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private int categoryId;

    public VideoCategoryMapping(Long videoId, int categoryId) {
        this.videoId = videoId;
        this.categoryId = categoryId;
    }
}
