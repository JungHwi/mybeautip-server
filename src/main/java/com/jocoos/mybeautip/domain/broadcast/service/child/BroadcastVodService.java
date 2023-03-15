package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.domain.vod.converter.VodConverter;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.service.dao.VodDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BroadcastVodService {

    private final VodDao vodDao;
    private final VodConverter vodConverter;

    @Transactional
    public void createVod(Broadcast broadcast) {
        Vod vod = vodConverter.init(broadcast);
        vodDao.save(vod);
    }
}
