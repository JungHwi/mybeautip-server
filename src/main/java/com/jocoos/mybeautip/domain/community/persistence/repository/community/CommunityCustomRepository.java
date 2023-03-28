package com.jocoos.mybeautip.domain.community.persistence.repository.community;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityCondition;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityCustomRepository {
    List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable);

    Page<AdminCommunityResponse> getCommunitiesAllStatus(CommunitySearchCondition condition);

    SearchResult<Community> search(KeywordSearchRequest condition);

    Long countBy(String keyword, Long memberId);

    List<SummaryCommunityResult> summary(SummaryCommunityCondition condition);
}
