package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastEditRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastReportDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditCommand;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.exception.ErrorCode.ALREADY_REPORT;

@RequiredArgsConstructor
@Service
public class BroadcastDomainService {

    private final BroadcastDao broadcastDao;
    private final BroadcastCategoryDao categoryDao;
    private final BroadcastReportDao reportDao;
    private final BroadcastConverter converter;
    private final BroadcastStatusService statusService;
    private final BroadcastFFLService fflService;

    @Transactional
    public Broadcast create(BroadcastCreateRequest request, long creatorId) {
        validIsMemberNotLive(creatorId);
        BroadcastCategory category = categoryDao.getCategory(request.getCategoryId());
        Broadcast broadcast = converter.toEntity(request, category, creatorId);

        ExternalBroadcastInfo externalInfo = fflService.createVideoRoom(broadcast);
        broadcast.updateVideoAndChannelKey(externalInfo.videoKey(), externalInfo.channelKey());
        return broadcastDao.save(broadcast);
    }

    @Transactional
    public BroadcastEditResult overwriteEdit(Long broadcastId, BroadcastEditRequest request, Long editorId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        BroadcastCategory editedCategory = getEditedCategory(broadcast, request.getCategoryId());
        String editedThumbnailUrl = request.getUploadThumbnailUrl(broadcast.getThumbnailUrl());
        BroadcastEditCommand editCommand = BroadcastEditCommand.edit(request, editedThumbnailUrl, editedCategory, editorId);
        return edit(broadcast, editCommand);
    }

    @Transactional
    public BroadcastEditResult partialEdit(Long broadcastId, BroadcastPatchRequest request, Long editorId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        BroadcastEditCommand patchCommand = BroadcastEditCommand.patch(broadcast, request, editorId);
        return edit(broadcast, patchCommand);
    }

    @Transactional
    public Broadcast changeStatus(long broadcastId, BroadcastStatus changeStatus) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        statusService.changeStatus(broadcast, changeStatus);
        return broadcast;
    }

    @Transactional
    public Broadcast report(long broadcastId, long reporterId, String description) {
        validIsFirstReport(broadcastId, reporterId);
        broadcastDao.addReportCountAndFlush(broadcastId, 1);
        Broadcast broadcast = broadcastDao.get(broadcastId);
        BroadcastReport report = new BroadcastReport(broadcast, reporterId, description);
        reportDao.save(report);
        return broadcast;
    }

    private BroadcastEditResult edit(Broadcast broadcast, BroadcastEditCommand command) {
        String originalThumbnailUrl = broadcast.getThumbnailUrl();
        broadcast.edit(command);
        Broadcast editedBroadcast = broadcastDao.save(broadcast);
        fflService.sendBroadcastEditedMessage(editedBroadcast);
        return new BroadcastEditResult(editedBroadcast, originalThumbnailUrl);
    }

    private BroadcastCategory getEditedCategory(Broadcast broadcast, Long categoryId) {
        if (broadcast.isCategoryEq(categoryId)) {
            return broadcast.getCategory();
        }
        return categoryDao.getCategory(categoryId);
    }

    private void validIsMemberNotLive(long creatorId) {
        if (broadcastDao.isCreatorLiveNow(creatorId)) {
            throw new BadRequestException("Already Live, Member Id :" + creatorId);
        }
    }

    private void validIsFirstReport(long broadcastId, long reporterId) {
        if (reportDao.exist(broadcastId, reporterId)) {
            throw new BadRequestException(ALREADY_REPORT);
        }
    }

    private void validCanEdit() {

    }
}
