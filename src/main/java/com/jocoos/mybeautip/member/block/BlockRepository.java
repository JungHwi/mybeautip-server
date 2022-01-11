package com.jocoos.mybeautip.member.block;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BlockRepository extends CrudRepository<Block, Long> {
  Optional<Block> findByMeAndMemberYouId(long me, long you);

  Page<Block> findByCreatedAtBeforeAndMe(Date startCursor, long me, Pageable pageable);

  List<Block> findByMe(long me);

  Optional<Block> findByIdAndMe(long id, long me);
}
