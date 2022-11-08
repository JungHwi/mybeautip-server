package com.jocoos.mybeautip.domain.community.persistence.repository.community;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityCondition;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityCustomRepository {
    List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable);

    Page<AdminCommunityResponse> getCommunitiesIncludeDelete(CommunitySearchCondition condition);

    SearchResult<Community> search(KeywordSearchCondition condition);
    Long countBy(String keyword);
    List<SummaryCommunityResult> summary(SummaryCommunityCondition condition);
}
