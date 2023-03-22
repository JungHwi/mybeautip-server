package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastPinMessage;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastPinMessageRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BroadcastPinChatDao {

    private final BroadcastPinMessageRepository repository;
    private final Session session;

    @Transactional
    public BroadcastPinMessage save(BroadcastPinMessage pinChat) {
        session.saveOrUpdate(pinChat);
        return pinChat;
    }

    @Transactional
    public void delete(Broadcast broadcast) {
        repository.deleteByBroadcast(broadcast);
    }

    @Transactional(readOnly = true)
    public Optional<BroadcastPinMessage> find(Broadcast broadcast) {
        return repository.findByBroadcast(broadcast);
    }
}
