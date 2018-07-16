package com.jocoos.mybeautip.member.following;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface FollowingRepository extends CrudRepository<Following, Long> {
  Optional<Following> findByMeAndYou(long me, long you);
  
  @Query("select f from Following f where f.me = :me and f.createdAt < :cursor order by f.createdAt desc")
  Slice<Following> findAllByMe(@Param("me")long me, @Param("cursor")Date cursor, Pageable of);
  
  @Query("select f from Following f where f.you = :you and f.createdAt < :cursor order by f.createdAt desc")
  Slice<Following> findAllByYou(@Param("you")long you, @Param("cursor")Date cursor, Pageable of);
}