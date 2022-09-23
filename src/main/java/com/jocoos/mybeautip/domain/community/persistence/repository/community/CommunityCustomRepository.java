package com.jocoos.mybeautip.domain.community.persistence.repository.community;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityCustomRepository {
    List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable);
    List<Community> search(KeywordSearchCondition condition);
}
