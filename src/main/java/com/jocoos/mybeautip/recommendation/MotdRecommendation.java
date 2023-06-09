package com.jocoos.mybeautip.recommendation;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.video.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "recommended_motds")
public class MotdRecommendation extends MemberAuditable {
    @Id
    @Column(name = "video_id")
    private Long videoId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(nullable = false)
    private int seq;

    @Column
    @LastModifiedDate
    private Date modifiedAt;

    @Column(nullable = false)
    private Long baseId;

    @Column
    private Date startedAt;

    @Column
    private Date endedAt;
}