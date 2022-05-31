package com.jocoos.mybeautip.video.view;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "video_views")
public class VideoView extends MemberAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column
    private String guestName;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column
    @LastModifiedDate
    private Date modifiedAt;

    public VideoView(Video video, Member member) {
        this.video = video;
        this.createdBy = member;
        this.viewCount = 1; // init
    }

    public VideoView(Video video, String guestName) {
        this.video = video;
        this.guestName = guestName;
        this.viewCount = 1; // init
    }
}