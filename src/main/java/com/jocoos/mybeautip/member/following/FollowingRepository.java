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
  
  List<Following> findByMemberMeId(Long me);

  List<Following> findByMemberYouId(Long you);
  
  List<Following> findByMemberMeId(Long me, Pageable pageable);
  
  List<Following> findByMemberYouId(Long me, Pageable pageable);
  
  List<Following> findByMemberMeIdAndMemberYouUsernameStartingWith(Long me, String keyword, Pageable pageable);
 
  List<Following> findByMemberYouIdAndMemberMeUsernameStartingWith(Long me, String keyword, Pageable pageable);
}