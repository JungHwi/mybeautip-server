package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.global.code.LikeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "video_likes")
public class VideoLike extends MemberAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column
    @Enumerated(EnumType.STRING)
    private LikeStatus status;

    public VideoLike(Video video) {
        this.video = video;
        this.status = LikeStatus.LIKE;
    }

    public void like() {
        this.status = LikeStatus.LIKE;
    }

    public void unlike() {
        this.status = LikeStatus.UNLIKE;
    }
}
