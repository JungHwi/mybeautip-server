package com.jocoos.mybeautip.domain.scrap.service.dao;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.persistence.repository.ScrapRepository;
import com.jocoos.mybeautip.domain.scrap.vo.ScrapSearchCondition;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.scrap.VideoScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.scrap.code.ScrapType.COMMUNITY;
import static com.jocoos.mybeautip.video.scrap.ScrapStatus.SCRAP;

@Service
@RequiredArgsConstructor
public class ScrapDao {

    private final ScrapRepository repository;
    private final VideoScrapRepository videoScrapRepository;

    @Transactional
    public Scrap scrap(ScrapRequest request) {
        Scrap scrap = getOrCreateScrap(request.getType(), request.getMemberId(), request.getRelationId());

        if (scrap.getIsScrap() == request.getIsScrap()) {
            return scrap;
        }

        scrap.setIsScrap(request.getIsScrap());

        return repository.save(scrap);
    }

    @Transactional
    public Scrap scrap(Scrap scrap) {
        return repository.save(scrap);
    }

    @Transactional(readOnly = true)
    public List<Scrap> getScraps(ScrapType type, long memberId, long cursor, Pageable pageable) {
        ScrapSearchCondition condition = new ScrapSearchCondition(type, memberId, true, cursor, pageable);
        return repository.getScraps(condition);
    }

    @Transactional(readOnly = true)
    public Scrap getOrCreateScrap(ScrapType type, long memberId, long relationId) {
        return repository.findByTypeAndMemberIdAndRelationId(type, memberId, relationId)
                .orElse(new Scrap(memberId, type, relationId));
    }

    @Transactional
    public List<Scrap> scrapCommunities(long memberId, List<Long> communityIds) {
        return repository.findByTypeAndMemberIdAndRelationIdInAndIsScrap(COMMUNITY, memberId, communityIds, true);
    }

    @Transactional(readOnly = true)
    public boolean isScrap(ScrapType type, Long memberId, Long relationId) {
        return repository.existsByTypeAndMemberIdAndRelationIdAndIsScrap(type, memberId, relationId, true);
    }

    @Transactional(readOnly = true)
    public boolean isExist(Member member) {
        return repository.existsByMemberIdAndIsScrap(member.getId(), true) ||
                // pageable for limit 1 query
                !videoScrapRepository
                .findOpenPublicVideoScraps(member, SCRAP, Pageable.ofSize(1))
                .getContent().isEmpty();
    }
}
