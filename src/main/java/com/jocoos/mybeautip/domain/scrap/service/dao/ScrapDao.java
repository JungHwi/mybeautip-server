package com.jocoos.mybeautip.domain.scrap.service.dao;

import com.jocoos.mybeautip.domain.community.converter.CommunityScrapConverter;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.persistence.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapDao {

    private final ScrapRepository repository;
    private final CommunityScrapConverter converter;

    @Transactional
    public Scrap scrap(ScrapRequest request) {
        Scrap scrap = getScrap(request.getType(), request.getMemberId(), request.getRelationId());

        if (scrap.getIsScrap() == request.getIsScrap()) {
            return scrap;
        }

        scrap.setIsScrap(request.getIsScrap());

        return repository.save(scrap);
    }

    @Transactional(readOnly = true)
    public List<Scrap> getScrapList(ScrapType type, long cursor, Pageable pageable) {
        return repository.findByTypeAndIdLessThan(type, cursor, pageable);
    }

    @Transactional(readOnly = true)
    public Scrap getScrap(ScrapType type, long memberId, long communityId) {
        return repository.findByTypeAndMemberIdAndRelationId(type, memberId, communityId)
                .orElse(new Scrap(memberId, type, communityId));
    }
}
