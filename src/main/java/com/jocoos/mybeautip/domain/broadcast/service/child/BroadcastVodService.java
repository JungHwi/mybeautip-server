package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.vod.converter.VodConverter;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.persistence.domain.VodIntegrationInfo;
import com.jocoos.mybeautip.domain.vod.service.dao.VodDao;
import com.jocoos.mybeautip.global.dto.IdAndBooleanResponse.IsVisibleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BroadcastVodService {

    private final VodDao vodDao;
    private final BroadcastDao broadcastDao;
    private final VodConverter vodConverter;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public void createVod(Broadcast broadcast) {
        Vod vod = vodConverter.init(broadcast);
        vodDao.save(vod);
    }

    @Transactional
    public IsVisibleResponse chooseVodVisibilityByEndOfBroadcast(Long broadcastId, boolean isVisible) {
        Vod vod = vodDao.getByBroadcastId(broadcastId);
        vod.changeVisibility(isVisible);
        return new IsVisibleResponse(vod.getId(), vod.isVisible());
    }

    @Transactional
    public void integrate(Long broadcastId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        Vod vod = vodDao.getByBroadcastId(broadcastId);
        vod.integrate(VodIntegrationInfo.from(broadcast));
        awsS3Handler.copyWithKeepOriginal(broadcast.getThumbnailUrl(), vod.getThumbnailPath());
    }
}
