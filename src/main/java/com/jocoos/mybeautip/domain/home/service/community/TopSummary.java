package com.jocoos.mybeautip.domain.home.service.community;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.home.converter.SummaryConverter;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryContentResponse;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryResponse;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;
import static com.jocoos.mybeautip.domain.home.code.SummaryCount.TOP_SUMMARY;

@RequiredArgsConstructor
@Component
public class TopSummary {

    private final CommunityDao communityDao;
    private final SummaryConverter summaryConverter;
    private final CommunityCategoryConverter categoryConverter;

    public TopSummaryResponse getResponse(List<CommunityCategory> topCategories) {
        List<CommunityCategoryResponse> categoryResponses =  categoryConverter.convert(topCategories);
        return new TopSummaryResponse(categoryResponses, getTopSummaryContents(categoryResponses));
    }

    private List<TopSummaryContentResponse> getTopSummaryContents(List<CommunityCategoryResponse> topCategories) {
        List<TopSummaryContentResponse> topSummaryContents = new ArrayList<>();
        for (CommunityCategoryResponse category : topCategories) {
            topSummaryContents.add(getTopSummaryContent(category.getId(), category.getType()));
        }
        return topSummaryContents;
    }

    private TopSummaryContentResponse getTopSummaryContent(Long categoryId, CommunityCategoryType type) {
        return new TopSummaryContentResponse(categoryId, communitySummaryFrom(categoryId, type));
    }

    private List<CommunityResponse> communitySummaryFrom(Long categoryId, CommunityCategoryType type) {
        List<SummaryCommunityResult> summaryResult = communityDao.summary(categoryId, type, TOP_SUMMARY.getCount());
        return convertSummaryByType(type, summaryResult);
    }

    private List<CommunityResponse> convertSummaryByType(CommunityCategoryType type, List<SummaryCommunityResult> tops) {
        if (DRIP.equals(type)) {
            return summaryConverter.convertDripSummary(tops);
        }
        return summaryConverter.convertNormalSummary(tops);
    }
}
