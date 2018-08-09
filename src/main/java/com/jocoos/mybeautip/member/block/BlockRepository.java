package com.jocoos.mybeautip.member.block;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

public interface BlockRepository extends CrudRepository<Block, Long> {
  Optional<Block> findByMeAndMemberYouId(long me, long you);

  Slice<Block> findByCreatedAtBeforeAndMe(Date startCursor, long me, Pageable pageable);
}