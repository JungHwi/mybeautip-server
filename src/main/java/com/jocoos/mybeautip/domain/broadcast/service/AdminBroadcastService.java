package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.EditBroadcastRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastReportDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.search.dto.CountResponse;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.dto.FileDto.getUploadAndDeleteFileDtoList;
import static com.jocoos.mybeautip.global.util.JsonNullableUtils.getIfPresent;

@RequiredArgsConstructor
@Service
public class AdminBroadcastService {

    private final BroadcastDao broadcastDao;
    private final BroadcastReportDao reportDao;
    private final BroadcastConverter converter;
    private final AwsS3Handler awsS3Handler;

    @Transactional(readOnly = true)
    public List<AdminBroadcastResponse> getList(BroadcastStatus status, SearchOption searchOption, Pageable pageable) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .pageable(pageable)
                .searchOption(searchOption)
                .build();
        List<BroadcastSearchResult> searchResults = broadcastDao.getList(condition);
        return converter.toAdminResponse(searchResults);
    }

    @Transactional(readOnly = true)
    public AdminBroadcastResponse get(long broadcastId) {
        BroadcastSearchResult searchResult = broadcastDao.getWithMemberAndCategory(broadcastId);
        return converter.toAdminResponse(searchResult);
    }

    @Transactional
    public Long edit(long broadcastId, EditBroadcastRequest request) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        String editedTitle = getIfPresent(request.getTitle(), broadcast.getTitle());
        String editedNotice = getIfPresent(request.getNotice(), broadcast.getNotice());

        String originalThumbnailUrl = broadcast.getThumbnailUrl();
        String editedThumbnailUrl = getIfPresent(request.getThumbnailUrl(), originalThumbnailUrl);
        broadcast.edit(editedTitle, editedNotice, editedThumbnailUrl);

        editThumbnailFile(editedThumbnailUrl, originalThumbnailUrl, broadcast.getId());
        return broadcast.getId();
    }

    @Transactional
    public void shutdown(long broadcastId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        broadcast.shutdown();

        // TODO FLIP FLOP LITE LOGIC NEEDED

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
