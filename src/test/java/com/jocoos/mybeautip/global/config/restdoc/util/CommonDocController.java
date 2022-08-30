package com.jocoos.mybeautip.global.config.restdoc.util;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.notification.code.*;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import com.jocoos.mybeautip.domain.point.code.PointStatus;
import com.jocoos.mybeautip.domain.point.code.PointStatusGroup;
import com.jocoos.mybeautip.domain.popup.code.ButtonLinkType;
import com.jocoos.mybeautip.domain.popup.code.PopupDisplayType;
import com.jocoos.mybeautip.domain.popup.code.PopupStatus;
import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.global.code.*;
import com.jocoos.mybeautip.global.config.restdoc.EnumDocs;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import com.jocoos.mybeautip.member.point.UsePointService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class CommonDocController {

    @GetMapping("/enums")
    public ApiResponseDto<EnumDocs> findEnums() {
        // Global
        Map<String, String> deviceOs = getDocs(DeviceOs.values());
        Map<String, String> language = getDocs(Language.values());
        Map<String, String> telecom = getDocs(Telecom.values());
        Map<String, String> urlDirectory = getDocs(UrlDirectory.values());
        Map<String, String> fileOperationType = getDocs(FileOperationType.values());

        // Member
        Map<String, String> memberStatus = getDocs(MemberStatus.values());
        Map<String, String> skinType = getDocs(SkinType.values());
        Map<String, String> skinWorry = getDocs(SkinWorry.values());

        // Event
        Map<String, String> eventStatus = getDocs(EventStatus.values());
        Map<String, String> eventJoinStatus = getDocs(EventJoinStatus.values());
        Map<String, String> eventProductType = getDocs(EventProductType.values());
        Map<String, String> eventType = getDocs(EventType.values());

        // Community
        Map<String, String> communityCategoryType = getDocs(CommunityCategoryType.values());
        Map<String, String> communityStatus = getDocs(CommunityStatus.values());

        // Notification
        Map<String, String> notificationLinkType = getDocs(NotificationLinkType.values());
        Map<String, String> messageType = getDocs(MessageType.values());
        Map<String, String> notificationArgument = getDocs(NotificationArgument.values());
        Map<String, String> notificationPlatform = getDocs(NotificationPlatform.values());
        Map<String, String> notificationStatus = getDocs(NotificationStatus.values());
        Map<String, String> sendType = getDocs(SendType.values());
        Map<String, String> templateType = getDocs(TemplateType.values());

        // Placard
        Map<String, String> placardLinkType = getDocs(PlacardLinkType.values());
        Map<String, String> placardStatus = getDocs(PlacardStatus.values());
        Map<String, String> placardTabType = getDocs(PlacardTabType.values());

        // Point
        Map<String, String> pointStatus = getDocs(PointStatus.values());
        Map<String, String> pointStatusGroup = getDocs(PointStatusGroup.values());
        Map<String, String> usePointService = getDocs(UsePointService.values());

        // Popup
        Map<String, String> buttonLinkType = getDocs(ButtonLinkType.values());
        Map<String, String> popupDisplayType = getDocs(PopupDisplayType.values());
        Map<String, String> popupStatus = getDocs(PopupStatus.values());

        // Term
        Map<String, String> termType = getDocs(TermType.values());

        return ApiResponseDto.of(EnumDocs.builder()
                // Global
                        .deviceOs(deviceOs)
                        .language(language)
                        .telecom(telecom)
                        .urlDirectory(urlDirectory)
                        .fileOperationType(fileOperationType)
                // Member
                        .memberStatus(memberStatus)
                        .skinType(skinType)
                        .skinWorry(skinWorry)
                // Event
                        .eventStatus(eventStatus)
                        .eventJoinStatus(eventJoinStatus)
                        .eventProductType(eventProductType)
                        .eventType(eventType)
                // Community
                        .communityCategoryType(communityCategoryType)
                        .communityStatus(communityStatus)
                // Notification
                        .notificationLinkType(notificationLinkType)
                        .messageType(messageType)
                        .notificationArgument(notificationArgument)
                        .notificationPlatform(notificationPlatform)
                        .notificationStatus(notificationStatus)
                        .sendType(sendType)
                        .templateType(templateType)
                // Placard
                        .placardLinkType(placardLinkType)
                        .placardStatus(placardStatus)
                        .placardTabType(placardTabType)
                // Point
                        .pointStatus(pointStatus)
                        .pointStatusGroup(pointStatusGroup)
                        .usePointService(usePointService)
                // Popup
                        .buttonLinkType(buttonLinkType)
                        .popupDisplayType(popupDisplayType)
                        .popupStatus(popupStatus)
                // Term
                        .termType(termType)
                .build()
        );
    }

    private Map<String, String> getDocs(CodeValue[] codeValues) {
        return Arrays.stream(codeValues)
                .collect(Collectors.toMap(CodeValue::getName, CodeValue::getDescription));
    }
}
