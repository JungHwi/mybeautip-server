package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.recommendation.MotdRecommendation;
import com.jocoos.mybeautip.restapi.LegacyVideoController;
import com.jocoos.mybeautip.video.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MotdDetailInfo extends LegacyVideoController.VideoInfo {
    private MotdRecommendation recommendation;
    private Long reportCount;
    private MemberInfo member;
    private Long videoReportId;

    public MotdDetailInfo(Video video) {
        BeanUtils.copyProperties(video, this);
        this.member = new MemberInfo(video.getMember());
    }

    public MotdDetailInfo(Video video, MotdRecommendation recommendation) {
        this(video);
        this.recommendation = recommendation;
    }
}