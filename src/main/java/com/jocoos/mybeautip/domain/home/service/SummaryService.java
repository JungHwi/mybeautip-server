package com.jocoos.mybeautip.domain.home.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryResponse;
import com.jocoos.mybeautip.domain.home.service.community.CommunitySummary;
import com.jocoos.mybeautip.domain.home.service.video.VideoSummary;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.supportsSummary;
import static com.jocoos.mybeautip.domain.home.code.SummaryType.VIDEO_SUMMARY;

@RequiredArgsConstructor
@Service
public class SummaryService {

    private final CommunitySummary communitySummary;
    private final VideoSummary videoSummary;
    private final LegacyMemberService legacyMemberService;

    @Transactional(readOnly = true)
    public TopSummaryResponse communityTop() {
        Long memberId = legacyMemberService.currentMemberId();
        return communitySummary.top(memberId);
    }

    @Transactional(readOnly = true)
    public List<VideoResponse> video() {
        return videoSummary.summary(VIDEO_SUMMARY.getCount());
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> community(CommunityCategoryType type) {
        if (!supportsSummary(type)) {
            throw new IllegalArgumentException("request type not supports in summary");
        }
        Long memberId = legacyMemberService.currentMemberId();
        return communitySummary.getSummary(type, memberId);
    }
}
