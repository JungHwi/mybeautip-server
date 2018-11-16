package com.jocoos.mybeautip.member.following;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

public interface FollowingRepository extends CrudRepository<Following, Long> {
  Optional<Following> findByMemberMeIdAndMemberYouId(Long me, Long you);
  
  Slice<Following> findByCreatedAtBeforeAndMemberMeId(Date createdAt, Long me, Pageable pageable);

  Slice<Following> findByCreatedAtBeforeAndMemberYouId(Date createdAt, Long you, Pageable pageable);

  List<Following> findByCreatedAtBeforeAndMemberYouId(Date createdAt, Long you);
  
  List<Following> findByMemberMeIdOrMemberYouId(Long me, Long you);
}