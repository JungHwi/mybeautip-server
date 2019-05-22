package com.jocoos.mybeautip.notification;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Optional<Notification> findByIdAndTargetMemberId(Long id, Long targetMember);

  Slice<Notification> findByTargetMemberId(Long targetMember, Pageable pageable);

  Slice<Notification> findByTargetMemberIdAndTypeNot(Long targetMember, String type, Pageable pageable);

  Slice<Notification> findByTargetMemberIdAndCreatedAtBefore(Long targetMember, Date createdAt, Pageable pageable);

  Slice<Notification> findByTargetMemberIdAndCreatedAtBeforeAndTypeNot(Long targetMember, Date createdAt, String type, Pageable pageable);

  int countByTargetMemberAndReadIsFalse(Member member);
  
  List<Notification> findByTargetMemberIdAndReadIsFalse(Long targetMember);
  
  List<Notification> findByResourceOwnerId(Long resourceOwnerId);
  
  List<Notification> findByTargetMemberId(Long targetMemberId);
  
  int countByTypeAndTargetMemberAndResourceIdAndResourceOwnerAndCreatedAtAfter(String type, Member targetMember, Long resourceId, Member resourceOwner, Date createdAt);
}
