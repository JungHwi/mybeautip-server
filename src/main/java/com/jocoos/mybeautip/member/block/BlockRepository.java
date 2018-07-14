package com.jocoos.mybeautip.member.block;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlockRepository extends CrudRepository<Block, Long> {
  Optional<Block> findByIAndYou(long i, long you);
  
  @Query("select b from Block b where b.i = :i and b.createdAt < :cursor order by b.createdAt desc")
  Slice<Block> findAllByI(@Param("i")long i, @Param("cursor")long cursor, Pageable of);
}