package com.jocoos.mybeautip.domain.community.persistence.repository.community;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.home.code.SummaryCommunityType;
import com.jocoos.mybeautip.domain.home.vo.SummaryResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityCustomRepository {
    List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable);
    SearchResult<Community> search(KeywordSearchCondition condition);
    Long countBy(String keyword);
    List<SummaryResult> summary(SummaryCommunityType condition);
}
