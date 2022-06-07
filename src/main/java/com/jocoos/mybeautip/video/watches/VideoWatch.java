package com.jocoos.mybeautip.video.watches;

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
@Table(name = "video_watches")
public class VideoWatch extends MemberAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    // username is used for guest
    // createdBy is used for member
    @Column
    private String username;

    @Column
    private Boolean isGuest = false;

    @Column
    @LastModifiedDate
    private Date modifiedAt;

    public VideoWatch(Video video, Member member) {
        this.video = video;
        this.createdBy = member;
    }

    public VideoWatch(Video video, String guestUsername) {
        this.video = video;
        this.username = guestUsername;
        this.isGuest = true;
    }
}