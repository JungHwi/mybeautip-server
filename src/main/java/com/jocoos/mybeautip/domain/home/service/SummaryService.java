package com.jocoos.mybeautip.domain.home.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.home.converter.SummaryConverter;
import com.jocoos.mybeautip.domain.home.dto.CommunitySummaryResponse;
import com.jocoos.mybeautip.domain.home.service.summary.TopSummary;
import com.jocoos.mybeautip.domain.home.vo.SummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SummaryService {

    private final CommunityDao communityDao;
    private final SummaryConverter summaryConverter;
    private final TopSummary topSummary;

    public CommunitySummaryResponse summary() {
        return new CommunitySummaryResponse(topSummary.getResponse(), getVoteSummary(), getBlindSummary());
    }

    private List<CommunityResponse> getBlindSummary() {
        List<SummaryResult> blinds = communityDao.summary(CommunityCategoryType.BLIND, 5);
        return summaryConverter.convertBlindSummary(blinds);
    }

    private List<CommunityResponse> getVoteSummary() {
        List<SummaryResult> votes = communityDao.summary(CommunityCategoryType.VOTE, 7);
        return summaryConverter.convertVoteSummary(votes);
    }
}
