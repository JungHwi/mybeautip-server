package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BlockRepository extends CrudRepository<Block, Long> {

    Page<Block> findByCreatedAtBeforeAndMeAndStatus(Date startCursor, long me, Pageable pageable, BlockStatus status);

    List<Block> findByMeAndStatus(long me, BlockStatus status);

    Optional<Block> findByIdAndMe(long id, long me);

    int countByMeAndMemberYouAndStatus(long memberId, Member member, BlockStatus status);

    Optional<Block> findByMeAndMemberYouIdAndStatus(Long memberId, Long targetId, BlockStatus status);

    Optional<Block> findByMeAndMemberYouId(Long memberId, Long targetId);
}
