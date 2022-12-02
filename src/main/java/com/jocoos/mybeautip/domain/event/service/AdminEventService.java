package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.converter.AdminEventConverter;
import com.jocoos.mybeautip.domain.event.dto.*;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.support.slack.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.util.FileUtil.isChange;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@RequiredArgsConstructor
@Service
public class AdminEventService {

    private final CommunityCategoryDao communityCategoryDao;
    private final EventDao eventDao;
    private final AdminEventConverter converter;
    private final AwsS3Handler awsS3Handler;
    private final SlackService slackService;

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

    @Transactional
    public AdminEventResponse edit(EditEventRequest request) {
        Event event = eventDao.getEvent(request.getId());
        copyOriginalEventFiles(event, request);

        event = eventDao.edit(event, request);

        editEventFile(request);
        return converter.convert(event);
    }

    @Transactional
    public EventBatchResult batchStatus() {
        int startEvent = startStatus();
        int endEvent = endStatus();

        if (startEvent > 0 || endEvent > 0) {
            String resultMessage = String.format("이벤트 자동 상태 변경 배치\n" +
                    "시작된 이벤트 : %d, 종료된 이벤트 : %d", startEvent, endEvent);
            slackService.send(resultMessage);
        }

        return EventBatchResult.builder()
                .startCount(startEvent)
                .endCount(endEvent)
                .build();
    }

    private int startStatus() {
        List<Event> eventList = eventDao.findStartEvent();
        List<Long> ids = eventList.stream()
                .map(Event::getId)
                .toList();
        return eventDao.updateStatus(ids, EventStatus.PROGRESS);
    }

    private int endStatus() {
        List<Event> eventList = eventDao.findEndEvent();
        List<Long> ids = eventList.stream()
                .map(Event::getId)
                .toList();
        return eventDao.updateStatus(ids, EventStatus.END);
    }

    private void copyOriginalEventFiles(Event event, EditEventRequest request) {
        request.setOriginalThumbnailImageFile(event.getThumbnailImageFile());
        request.setOriginalDetailImageFile(event.getImageFile());
        request.setOriginalShareRectangleImageFile(event.getShareRectangleImageFile());
        request.setOriginalShareSquareImageFile(event.getShareSquareImageFile());
        request.setOriginalBannerImageFile(event.getBannerImageFile());
    }

    private void copyEventFile(EventRequest request) {
        awsS3Handler.copy(request.getThumbnailImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getDetailImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getShareRectangleImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getShareSquareImageUrl(), UrlDirectory.EVENT.getDirectory());
        awsS3Handler.copy(request.getBannerImageUrl(), UrlDirectory.EVENT.getDirectory());
    }

    private void editEventFile(EditEventRequest request) {
        if (isChange(request.getOriginalThumbnailImageFile(), request.getThumbnailImageUrl())) {
            awsS3Handler.copy(request.getThumbnailImageUrl(), UrlDirectory.EVENT.getDirectory());
            awsS3Handler.delete(toUrl(request.getOriginalThumbnailImageFile(), UrlDirectory.EVENT));
        }

        if (isChange(request.getOriginalDetailImageFile(), request.getDetailImageUrl())) {
            awsS3Handler.copy(request.getDetailImageUrl(), UrlDirectory.EVENT.getDirectory());
            awsS3Handler.delete(toUrl(request.getOriginalDetailImageFile(), UrlDirectory.EVENT));
        }

        if (isChange(request.getOriginalShareSquareImageFile(), request.getShareSquareImageUrl())) {
            awsS3Handler.copy(request.getShareSquareImageUrl(), UrlDirectory.EVENT.getDirectory());
            awsS3Handler.delete(toUrl(request.getOriginalShareSquareImageFile(), UrlDirectory.EVENT));
        }

        if (isChange(request.getOriginalShareRectangleImageFile(), request.getShareRectangleImageUrl())) {
            awsS3Handler.copy(request.getShareRectangleImageUrl(), UrlDirectory.EVENT.getDirectory());
            awsS3Handler.delete(toUrl(request.getOriginalShareRectangleImageFile(), UrlDirectory.EVENT));
        }

        if (isChange(request.getOriginalBannerImageFile(), request.getBannerImageUrl())) {
            awsS3Handler.copy(request.getBannerImageUrl(), UrlDirectory.EVENT.getDirectory());
            awsS3Handler.delete(toUrl(request.getOriginalBannerImageFile(), UrlDirectory.EVENT));
        }
    }

    public Long getRelationIdByType(EventRequest request) {
        if (request.getType() == EventType.DRIP) {
            CommunityCategory communityCategory = communityCategoryDao.getByType(CommunityCategoryType.DRIP);
            return communityCategory.getId();
        }
        return null;
    }
}
