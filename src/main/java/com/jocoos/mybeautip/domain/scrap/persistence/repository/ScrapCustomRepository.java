package com.jocoos.mybeautip.domain.scrap.persistence.repository;

import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.vo.ScrapSearchCondition;

import java.util.List;

public interface ScrapCustomRepository {
    List<Scrap> getScrapsExcludeBlockMember(ScrapSearchCondition condition);
}
