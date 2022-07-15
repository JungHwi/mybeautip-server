package com.jocoos.mybeautip.domain.friend.persistence.repository;

import com.jocoos.mybeautip.domain.friend.persistence.domain.FriendInviteInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendInviteInfoRepository extends JpaRepository<FriendInviteInfo, Long> {
}
