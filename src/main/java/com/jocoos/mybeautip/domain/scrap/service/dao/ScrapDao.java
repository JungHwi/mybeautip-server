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
        Scrap scrap = getScrap(request.getType(), request.getMemberId(), request.getRelationId());

        if (scrap.getIsScrap() == request.getIsScrap()) {
            return scrap;
        }

        scrap.setIsScrap(request.getIsScrap());

        return repository.save(scrap);
    }

    @Transactional(readOnly = true)
    public List<Scrap> getScrapList(ScrapType type, long memberId, long cursor, Pageable pageable) {
        ScrapSearchCondition condition = new ScrapSearchCondition(type, memberId, true, cursor, pageable);
        return repository.getScrapsExcludeBlockMember(condition);
    }

    @Transactional(readOnly = true)
    public Scrap getScrap(ScrapType type, long memberId, long communityId) {
        return repository.findByTypeAndMemberIdAndRelationId(type, memberId, communityId)
                .orElse(new Scrap(memberId, type, communityId));
    }

    public List<Scrap> scrapCommunities(long memberId, List<Long> communityIds) {
        return repository.findByTypeAndMemberIdAndRelationIdInAndIsScrap(COMMUNITY, memberId, communityIds, true);
    }

    public Boolean isScrapCommunity(Long memberId, Long communityId) {
        return repository.existsByTypeAndMemberIdAndRelationIdAndIsScrap(COMMUNITY, memberId, communityId, true);
    }

    public boolean isExist(Member member) {
        return repository.existsByMemberIdAndIsScrap(member.getId(), true)
                || videoScrapRepository.existsByCreatedByAndStatus(member, SCRAP);
    }
}
