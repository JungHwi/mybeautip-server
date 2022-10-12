package com.jocoos.mybeautip.domain.home.service.community;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.CommunityRelationService;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.home.code.SummaryType;
import com.jocoos.mybeautip.domain.home.converter.SummaryConverter;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryResponse;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CommunitySummary {

    private final CommunityDao communityDao;
    private final CommunityCategoryDao categoryDao;
    private final CommunityCategoryConverter categoryConverter;
    private final SummaryConverter summaryConverter;
    private final TopSummary topSummary;
    private final CommunityRelationService relationService;


    @Transactional(readOnly = true)
    public TopSummaryResponse top() {
        List<CommunityCategory> topCategories = categoryDao.topSummaryCategories();
        List<CommunityCategoryResponse> categoryResponses =  categoryConverter.convert(topCategories);
        return topSummary.getResponse(categoryResponses);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getSummary(CommunityCategoryType categoryType) {
        SummaryType summaryType = SummaryType.getByCategory(categoryType);
        CommunityCategory category = categoryDao.getByType(categoryType);
        List<SummaryCommunityResult> summaryResults = communityDao.summary(category.getId(), categoryType, summaryType.getCount());
        List<CommunityResponse> responses = summaryConverter.convert(summaryResults);
        return relationService.setRelationInfo(responses);
    }
}
