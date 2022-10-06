package com.jocoos.mybeautip.domain.home.service.community;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.home.converter.SummaryConverter;
import com.jocoos.mybeautip.domain.home.dto.CommunitySummaryResponse;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryResponse;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;
import static com.jocoos.mybeautip.domain.home.code.SummaryCount.BLIND_SUMMARY;
import static com.jocoos.mybeautip.domain.home.code.SummaryCount.VOTE_SUMMARY;

@RequiredArgsConstructor
@Component
public class CommunitySummary {

    private final CommunityDao communityDao;
    private final CommunityCategoryDao categoryDao;
    private final SummaryConverter summaryConverter;
    private final TopSummary topSummary;


    @Transactional(readOnly = true)
    public CommunitySummaryResponse summary() {

        List<CommunityCategory> allSummaryCategories = categoryDao.allSummaryCategories();

        TopSummaryResponse topSummaryResponse = topSummary.getResponse(getTopCategories(allSummaryCategories));
        List<CommunityResponse> voteSummaryResponse = voteSummary(getCategoryId(allSummaryCategories, VOTE));
        List<CommunityResponse> blindSummaryResponse = blindSummary(getCategoryId(allSummaryCategories, BLIND));

        return new CommunitySummaryResponse(topSummaryResponse, voteSummaryResponse, blindSummaryResponse);
    }

    public List<CommunityResponse> blindSummary(Long blindCategoryId) {
        List<SummaryCommunityResult> blinds = communityDao.summary(blindCategoryId, BLIND, BLIND_SUMMARY.getCount());
        return summaryConverter.convertBlindSummary(blinds);
    }

    private List<CommunityResponse> voteSummary(Long voteCategoryId) {
        List<SummaryCommunityResult> votes = communityDao.summary(voteCategoryId, VOTE, VOTE_SUMMARY.getCount());
        return summaryConverter.convertVoteSummary(votes);
    }

    private List<CommunityCategory> getTopCategories(List<CommunityCategory> categories) {
        return categories
                .stream()
                .filter(this::isTopCategory)
                .collect(Collectors.toList());
    }

    private Long getCategoryId(List<CommunityCategory> categories, CommunityCategoryType type) {
        return categories.stream()
                .filter(category -> category.isCategoryType(type))
                .findFirst()
                .map(CommunityCategory::getId)
                .orElse(null);
    }

    private boolean isTopCategory(CommunityCategory category) {
        return !VOTE.equals(category.getType()) && !BLIND.equals(category.getType());
    }
}
