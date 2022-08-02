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
    Map<String, String> deviceOs;
    Map<String, String> language;
    Map<String, String> telecom;
    Map<String, String> urlDirectory;

    // Member
    Map<String, String> memberStatus;
    Map<String, String> skinType;
    Map<String, String> skinWorry;

    // Event
    Map<String, String> eventStatus;
    Map<String, String> eventJoinStatus;
    Map<String, String> eventProductType;
    Map<String, String> eventType;

    // Community
    Map<String, String> communityCategoryType;
    Map<String, String> postLikeStatus;

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

    // Term
    Map<String, String> termType;
}
