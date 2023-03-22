package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastParticipantInfo;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.HeartCountResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastReportResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastDomainService;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastParticipantInfoService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastReportDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.search.dto.CountResponse;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.END;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;
import static com.jocoos.mybeautip.global.dto.FileDto.uploadAndDeleteImages;

@RequiredArgsConstructor
@Service
public class AdminBroadcastService {

    private final BroadcastDao broadcastDao;
    private final BroadcastReportDao reportDao;
    private final BroadcastConverter converter;
    private final BroadcastDomainService domainService;
    private final BroadcastParticipantInfoService participantInfoService;
    private final AwsS3Handler awsS3Handler;

    @Transactional(readOnly = true)
    public PageResponse<AdminBroadcastResponse> getList(BroadcastStatus status, SearchOption searchOption, Pageable pageable) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .pageable(pageable)
                .searchOption(searchOption)
                .build();
        Page<BroadcastSearchResult> searchResults = broadcastDao.getPageList(condition);
        List<AdminBroadcastResponse> response = converter.toAdminResponse(searchResults.getContent());
        return new PageResponse<>(searchResults.getTotalElements(), response);
    }

    @Transactional(readOnly = true)
    public AdminBroadcastResponse get(Long broadcastId, String requestUsername) {
        BroadcastSearchResult searchResult = broadcastDao.getSearchResult(broadcastId);
        BroadcastParticipantInfo participantInfo = participantInfoService.getParticipantInfo(requestUsername, searchResult);
        return converter.toAdminResponse(searchResult, participantInfo);
    }

    @Transactional
    public Long edit(Long broadcastId, BroadcastPatchRequest request, Long editorId) {
        BroadcastEditResult editResult = domainService.partialEdit(broadcastId, request, editorId);
        editThumbnailFile(editResult);
        return broadcastId;
    }

    @Transactional
    public void shutdown(long broadcastId) {
        domainService.changeStatus(broadcastId, END);
    }

    @Transactional(readOnly = true)
    public CountResponse countReportedBroadcast(ZonedDateTime startAt) {
        return new CountResponse(reportDao.countReportedBroadcast(startAt));
    }

    public HeartCountResponse addHeartCount(Long broadcastId, int heartCount) {
        Broadcast broadcast = domainService.addHeartCount(broadcastId, heartCount);
        return new HeartCountResponse(broadcast.getId(), broadcast.getHeartCount());
    }

    @Transactional(readOnly = true)
    public HeartCountResponse getHeartCount(Long broadcastId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        return new HeartCountResponse(broadcast.getId(), broadcast.getHeartCount());
    }

    @Transactional(readOnly = true)
    public PageResponse<BroadcastReportResponse> getReports(Long broadcastId,
                                                    BroadcastReportType type,
                                                    Pageable pageable) {
        return new PageResponse<>(reportDao.getList(broadcastId, type, pageable));
    }

    private void editThumbnailFile(BroadcastEditResult editResult) {
        if (editResult.isThumbnailChanged()) {
            Broadcast broadcast = editResult.broadcast();
            String originalThumbnailUrl = editResult.originalInfo().thumbnailUrl();
            String editedThumbnailUrl = broadcast.getThumbnailUrl();
            List<FileDto> files = uploadAndDeleteImages(editedThumbnailUrl, originalThumbnailUrl);
            awsS3Handler.editFiles(files, broadcast.getThumbnailUrlPath());
        }
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? null : getSearchStatuses(status);
    }
}
