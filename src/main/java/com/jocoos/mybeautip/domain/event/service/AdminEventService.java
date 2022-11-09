package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.converter.AdminEventConverter;
import com.jocoos.mybeautip.domain.event.dto.AdminEventResponse;
import com.jocoos.mybeautip.domain.event.dto.EventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminEventService {

    private final CommunityCategoryDao communityCategoryDao;
    private final EventDao eventDao;
    private final AdminEventConverter converter;
    private final AwsS3Handler awsS3Handler;

    @Transactional(readOnly = true)
    public List<EventStatusResponse> getEventStates() {
        Map<EventStatus, Long> joinCountMap = eventDao.getJoinCountMapGroupByEventStatus();
        return converter.convert(joinCountMap);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminEventResponse> getEvents(EventSearchCondition condition) {
        List<EventSearchResult> events = eventDao.getEventsWithJoinCount(condition);
        Long totalCount = eventDao.getTotalCount(condition);
        return new PageResponse<>(totalCount, converter.convert(events));
    }

    @Transactional(readOnly = true)
    public AdminEventResponse getEvent(long eventId) {
        Event event = eventDao.getEvent(eventId);
        Long joinCount = eventDao.getJoinCount(event);
        return converter.convertWithAllImages(event, joinCount);
    }

    public List<String> upload(List<MultipartFile> files) {
        return awsS3Handler.upload(files, UrlDirectory.TEMP.getDirectory());
    }

    @Transactional
    public AdminEventResponse create(EventRequest request) {
        Long relationId = getRelationIdByType(request);
        request.setRelationId(relationId);
        Event event = eventDao.create(request);
        copyEventFile(request);
        return converter.convert(event);
    }

    private void copyEventFile(EventRequest request) {
        awsS3Handler.copy(request.getThumbnailImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getDetailImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getShareRectangleImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getShareSquareImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getBannerImageUrl(), UrlDirectory.EVENT.getDirectory());
    }

    public Long getRelationIdByType(EventRequest request) {
        if (request.getType() == EventType.DRIP) {
            CommunityCategory communityCategory = communityCategoryDao.getByType(CommunityCategoryType.DRIP);
            return communityCategory.getId();
        }
        return null;
    }
}
