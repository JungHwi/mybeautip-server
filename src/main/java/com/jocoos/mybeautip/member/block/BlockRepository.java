package com.jocoos.mybeautip.member.block;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BlockRepository extends CrudRepository<Block, Long> {

    Page<Block> findByCreatedAtBeforeAndMeAndStatus(Date startCursor, long me, Pageable pageable, BlockStatus status);

    Optional<Block> findByIdAndMe(long id, long me);

    Optional<Block> findByMeAndMemberYouIdAndStatus(Long memberId, Long targetId, BlockStatus status);

    Optional<Block> findByMeAndMemberYouId(Long memberId, Long targetId);

    boolean existsByMeAndMemberYouIdAndStatus(Long memberId, Long targetId, BlockStatus status);

    List<Block> findAllByMeAndMemberYouIdInAndStatus(Long memberId, List<Long> targetIds, BlockStatus status);
}
