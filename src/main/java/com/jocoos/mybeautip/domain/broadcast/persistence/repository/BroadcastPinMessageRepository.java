package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastPinMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BroadcastPinMessageRepository extends JpaRepository<BroadcastPinMessage, Long> {

    Optional<BroadcastPinMessage> findByBroadcast(Broadcast broadcast);
    void deleteByBroadcast(Broadcast broadcast);
}
