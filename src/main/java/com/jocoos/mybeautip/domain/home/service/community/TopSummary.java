package com.jocoos.mybeautip.domain.home.service.community;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.service.CommunityRelationService;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.home.converter.SummaryConverter;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryContentResponse;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryResponse;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.domain.home.code.SummaryType.getCountOfTopSummary;

@RequiredArgsConstructor
@Component
public class TopSummary {

    private final CommunityDao communityDao;
    private final SummaryConverter summaryConverter;
    private final CommunityRelationService relationService;

    public TopSummaryResponse getResponse(List<CommunityCategoryResponse> topCategories, Long memberId) {
        return new TopSummaryResponse(topCategories, getTopSummaryContents(topCategories, memberId));
    }

    private List<TopSummaryContentResponse> getTopSummaryContents(List<CommunityCategoryResponse> topCategories, Long memberId) {
        List<TopSummaryContentResponse> topSummaryContents = new ArrayList<>();
        for (CommunityCategoryResponse category : topCategories) {
            topSummaryContents.add(getTopSummaryContent(category.getId(), category.getType(), memberId));
        }
        return topSummaryContents;
    }

    private TopSummaryContentResponse getTopSummaryContent(Long categoryId, CommunityCategoryType type, Long memberId) {
        return new TopSummaryContentResponse(categoryId, communitySummaryFrom(categoryId, type, memberId));
    }

    private List<CommunityResponse> communitySummaryFrom(Long categoryId, CommunityCategoryType type, Long memberId) {
        List<SummaryCommunityResult> summaryResult = communityDao.summary(categoryId, type, getCountOfTopSummary(type), memberId);
        List<CommunityResponse> responses = summaryConverter.convert(summaryResult);
        return relationService.setRelationInfo(responses);
    }
}
