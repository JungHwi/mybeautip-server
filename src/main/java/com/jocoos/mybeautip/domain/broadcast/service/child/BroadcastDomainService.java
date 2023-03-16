package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
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
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult.OriginalInfo;
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
    public int broadcastReport(long broadcastId, long reporterId, String reason) {
        validIsFirstReport(broadcastId, BroadcastReportType.BROADCAST, reporterId);
        broadcastDao.addReportCountAndFlush(broadcastId, 1);
        Broadcast broadcast = broadcastDao.get(broadcastId);
        BroadcastReport report = BroadcastReport.builder()
                .type(BroadcastReportType.BROADCAST)
                .broadcastId(broadcastId)
                .reporterId(reporterId)
                .reportedId(broadcast.getMemberId())
                .reason(reason)
                .build();
        reportDao.save(report);
        return broadcast.getReportCount();
    }

    @Transactional
    public int messageReport(long broadcastId, long reporterId, long reportedId, String reason, String description) {
        validIsFirstReport(broadcastId, BroadcastReportType.MESSAGE, reporterId);
        BroadcastReport report = BroadcastReport.builder()
                .type(BroadcastReportType.MESSAGE)
                .broadcastId(broadcastId)
                .reporterId(reporterId)
                .reportedId(reportedId)
                .reason(reason)
                .description(description)
                .build();
        reportDao.save(report);
        return broadcastDao.get(broadcastId).getReportCount();
    }

    private BroadcastEditResult edit(Broadcast broadcast, BroadcastEditCommand command) {
        OriginalInfo originalInfo = new OriginalInfo(broadcast);
        broadcast.edit(command);
        Broadcast editedBroadcast = broadcastDao.save(broadcast);
        BroadcastEditResult editResult = new BroadcastEditResult(editedBroadcast, originalInfo);
        fflService.sendBroadcastEditedMessage(editResult);
        return editResult;
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

    private void validIsFirstReport(long broadcastId, BroadcastReportType type, long reporterId) {
        if (reportDao.exist(broadcastId, type, reporterId)) {
            throw new BadRequestException(ALREADY_REPORT);
        }
    }

    private void validCanEdit() {
        // TODO

    }
}
