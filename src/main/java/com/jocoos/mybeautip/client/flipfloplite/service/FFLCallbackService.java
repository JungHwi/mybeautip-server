package com.jocoos.mybeautip.client.flipfloplite.service;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLChangeStatusData;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class FFLCallbackService {

    private final BroadcastDao broadcastDao;

    @Transactional
    public void changeStatus(FFLChangeStatusData request) {
        broadcastDao.updatePausedAt(request.videoRoomId(), getPausedAt(request.videoRoomVideoRoomState()));
    }
    
    private ZonedDateTime getPausedAt(FFLVideoRoomState videoRoomState) {
        return switch (videoRoomState) {
            case LIVE -> null;
            case LIVE_INACTIVE -> ZonedDateTime.now();
            default -> throw new BadRequestException("Invalid video room state");
        };
    }
}
