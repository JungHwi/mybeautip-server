package com.jocoos.mybeautip.domain.home.service.summary;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.home.converter.SummaryConverter;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryContentResponse;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryResponse;
import com.jocoos.mybeautip.domain.home.vo.SummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;

@RequiredArgsConstructor
@Component
public class TopSummary {

    private final CommunityDao communityDao;
    private final CommunityCategoryDao categoryDao;
    private final SummaryConverter summaryConverter;
    private final CommunityCategoryConverter categoryConverter;

    public TopSummaryResponse getResponse() {
        List<CommunityCategoryResponse> categoryResponses = getTopCategories();
        return new TopSummaryResponse(categoryResponses, getTopSummaryContents(categoryResponses));
    }

    private List<CommunityCategoryResponse> getTopCategories() {
        List<CommunityCategory> categories = categoryDao.allSummaryCategories();
        return categoryConverter.convert(categories);
    }

    private List<TopSummaryContentResponse> getTopSummaryContents(List<CommunityCategoryResponse> topCategories) {
        List<TopSummaryContentResponse> topSummaryContents = new ArrayList<>();
        for (CommunityCategoryResponse category : topCategories) {
            topSummaryContents.add(getTopSummaryContent(category.getId(), category.getType()));
        }
        return topSummaryContents;
    }

    private TopSummaryContentResponse getTopSummaryContent(Long categoryId, CommunityCategoryType type) {
        return new TopSummaryContentResponse(categoryId, communityResponsesFrom(categoryId, type));
    }

    private List<CommunityResponse> communityResponsesFrom(Long categoryId, CommunityCategoryType type) {
        List<SummaryResult> summaryResult = communityDao.summary(categoryId, type, 3);
        return convertSummaryByType(type, summaryResult);
    }

    private List<CommunityResponse> convertSummaryByType(CommunityCategoryType type, List<SummaryResult> tops) {
        if (DRIP.equals(type)) {
            return summaryConverter.convertDripSummary(tops);
        }
        return summaryConverter.convertNormalSummary(tops);
    }
}
