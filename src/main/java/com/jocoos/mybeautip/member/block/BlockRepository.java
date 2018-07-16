package com.jocoos.mybeautip.member.block;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface BlockRepository extends CrudRepository<Block, Long> {
  Optional<Block> findByMeAndYou(long me, long you);
  
  @Query("select b from Block b where b.me = :me and b.createdAt < :cursor order by b.createdAt desc")
  Slice<Block> findAllByMe(@Param("me")long me, @Param("cursor")Date cursor, Pageable of);
}