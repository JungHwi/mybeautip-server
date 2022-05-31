package com.jocoos.mybeautip.video.report;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "video_reports")
public class VideoReport extends MemberAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column
    private Integer reasonCode;

    @Column(nullable = false)
    private String reason;

    public VideoReport(Video video, Member member, int reasonCode, String reason) {
        this.video = video;
        this.createdBy = member;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }
}