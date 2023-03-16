package com.jocoos.mybeautip.global.config.restdoc;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumDocs {

    // Global
    Map<String, String> booleanType;
    Map<String, String> deviceOs;
    Map<String, String> language;
    Map<String, String> telecom;
    Map<String, String> urlDirectory;
    Map<String, String> fileOperationType;
    Map<String, String> fileType;
    Map<String, String> sortField;

    // System
    Map<String, String> systemOptionType;

    // Member
    Map<String, String> memberStatus;
    Map<String, String> influencerStatus;
    Map<String, String> skinType;
    Map<String, String> skinWorry;
    Map<String, String> grantType;
    Map<String, String> role;

    // Broadcast
    Map<String, String> broadcastViewerType;
    Map<String, String> broadcastViewerStatus;
    Map<String, String> broadcastReportType;
    Map<String, String> broadcastStatus;
    Map<String, String> broadcastSortField;

    // Event
    Map<String, String> eventStatus;
    Map<String, String> eventJoinStatus;
    Map<String, String> eventProductType;
    Map<String, String> eventType;

    // Community
    Map<String, String> communityStatus;
    Map<String, String> communityCategoryType;

    // Video
    Map<String, String> videoCategoryType;
    Map<String, String> videoMaskType;
    Map<String, String> videoCommentStatus;
    Map<String, String> videoStatus;

    // Notification
    Map<String, String> notificationLinkType;
    Map<String, String> messageType;
    Map<String, String> notificationArgument;
    Map<String, String> notificationPlatform;
    Map<String, String> notificationStatus;
    Map<String, String> sendType;
    Map<String, String> templateType;

    // Placard
    Map<String, String> placardLinkType;
    Map<String, String> placardStatus;
    Map<String, String> placardTabType;

    // Point
    Map<String, String> pointStatus;
    Map<String, String> pointStatusGroup;
    Map<String, String> usePointService;

    // Popup
    Map<String, String> buttonLinkType;
    Map<String, String> popupDisplayType;
    Map<String, String> popupStatus;

    // Popup Notice
    Map<String, String> popupNoticeLinkType;

    // Term
    Map<String, String> termType;

    // Search
    Map<String, String> searchType;

    // Scrap
    Map<String, String> scrapType;

    // Operation
    Map<String, String> operationTargetType;
    Map<String, String> operationType;

    // Notice
    Map<String, String> noticeStatus;
    Map<String, String> noticeSort;

    // Vod
    Map<String, String> vodSortField;
}
