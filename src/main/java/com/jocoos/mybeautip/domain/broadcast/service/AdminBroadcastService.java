package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastReportDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditCommand;
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

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.CANCEL;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.dto.FileDto.getUploadAndDeleteFileDtoList;

@RequiredArgsConstructor
@Service
public class AdminBroadcastService {

    private final BroadcastDao broadcastDao;
    private final BroadcastReportDao reportDao;
    private final BroadcastConverter converter;
    private final FlipFlopLiteService flipFlopLiteService;
    private final AwsS3Handler awsS3Handler;

    @Transactional(readOnly = true)
    public PageResponse<AdminBroadcastResponse> getList(BroadcastStatus status, SearchOption searchOption, Pageable pageable) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .pageable(pageable)
                .searchOption(searchOption)
                .build();
        Page<BroadcastSearchResult> searchResults = broadcastDao.getList(condition);
        List<AdminBroadcastResponse> response = converter.toAdminResponse(searchResults.getContent());
        return new PageResponse<>(searchResults.getTotalElements(), response);
    }

    @Transactional(readOnly = true)
    public AdminBroadcastResponse get(long broadcastId) {
        BroadcastSearchResult searchResult = broadcastDao.getWithMemberAndCategory(broadcastId);
        return converter.toAdminResponse(searchResult);
    }

    @Transactional
    public Long edit(long broadcastId, BroadcastPatchRequest request) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        String originalThumbnailUrl = broadcast.getThumbnailUrl();

        BroadcastEditCommand patchCommand = BroadcastEditCommand.patch(broadcast, request);
        broadcast.edit(patchCommand);

        editThumbnailFile(patchCommand.getEditedThumbnail(), originalThumbnailUrl, broadcast.getId());
        return broadcast.getId();
    }

    @Transactional
    public void shutdown(long broadcastId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        ZonedDateTime endedAt = flipFlopLiteService.endVideoRoom(broadcast.getVideoKey());
        broadcast.finish(CANCEL, endedAt);
    }

    @Transactional(readOnly = true)
    public CountResponse countReportedBroadcast(ZonedDateTime startAt) {
        return new CountResponse(reportDao.countReportedBroadcast(startAt));
    }

    private void editThumbnailFile(String editedThumbnailUrl, String originalThumbnailUrl, long broadcastId) {
        if (!originalThumbnailUrl.equals(editedThumbnailUrl)) {
            List<FileDto> files = getUploadAndDeleteFileDtoList(editedThumbnailUrl, originalThumbnailUrl);
            awsS3Handler.editFiles(files, BROADCAST.getDirectory(broadcastId));
        }
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? null : getSearchStatuses(status);
    }
}
