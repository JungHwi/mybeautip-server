package com.jocoos.mybeautip.follow.member;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends CrudRepository<Follow, Long> {
  Optional<Follow> findByIAndYou(long i, long you);
  
  @Query("select f from Follow f where f.i = :i and f.createdAt < :cursor order by f.createdAt desc")
  Slice<Follow> findAllByI(@Param("i")long i, @Param("cursor")long cursor, Pageable of);
  
  @Query("select f from Follow f where f.you = :you and f.createdAt < :cursor order by f.createdAt desc")
  Slice<Follow> findAllByYou(@Param("you")long you, @Param("cursor")long cursor, Pageable of);
}