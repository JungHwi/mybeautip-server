package com.jocoos.mybeautip.domain.home.service;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.home.code.SummaryCommunityType;
import com.jocoos.mybeautip.domain.home.converter.SummaryConverter;
import com.jocoos.mybeautip.domain.home.dto.CommunitySummaryResponse;
import com.jocoos.mybeautip.domain.home.vo.SummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jocoos.mybeautip.domain.home.code.SummaryCommunityType.*;

@RequiredArgsConstructor
@Service
public class SummaryService {

    private final CommunityDao communityDao;
    private final SummaryConverter summaryConverter;

    public CommunitySummaryResponse summary(SummaryCommunityType topType) {
        return new CommunitySummaryResponse(getTopSummary(topType), getVoteSummary(), getBlindSummary());
    }

    private List<CommunityResponse> getTopSummary(SummaryCommunityType topType) {
        List<SummaryResult> tops = communityDao.summary(topType);
        return summaryByType(topType, tops);
    }

    private List<CommunityResponse> getBlindSummary() {
        List<SummaryResult> blinds = communityDao.summary(BLIND_SUMMARY);
        return summaryConverter.convertBlindSummary(blinds);
    }

    private List<CommunityResponse> getVoteSummary() {
        List<SummaryResult> votes = communityDao.summary(VOTE_SUMMARY);
        return summaryConverter.convertVoteSummary(votes);
    }

    private List<CommunityResponse> summaryByType(SummaryCommunityType type, List<SummaryResult> tops) {
        if (PICK_SUMMARY.equals(type)) {
            return summaryConverter.convertPickSummary(tops);
        }
        return summaryConverter.convertNormalSummary(tops);
    }
}
