package com.jocoos.mybeautip.global.config.restdoc.util;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;
import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import com.jocoos.mybeautip.domain.broadcast.code.*;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.code.ProcessPermission;
import com.jocoos.mybeautip.domain.delivery.code.*;
import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.domain.member.code.*;
import com.jocoos.mybeautip.domain.notice.code.NoticeSort;
import com.jocoos.mybeautip.domain.notice.code.NoticeStatus;
import com.jocoos.mybeautip.domain.notification.code.*;
import com.jocoos.mybeautip.domain.operation.code.OperationTargetType;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import com.jocoos.mybeautip.domain.point.code.PointStatus;
import com.jocoos.mybeautip.domain.point.code.PointStatusGroup;
import com.jocoos.mybeautip.domain.popup.code.ButtonLinkType;
import com.jocoos.mybeautip.domain.popup.code.PopupDisplayType;
import com.jocoos.mybeautip.domain.popup.code.PopupStatus;
import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeLinkType;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;
import com.jocoos.mybeautip.domain.system.code.SystemOptionType;
import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.domain.video.code.VideoCategoryType;
import com.jocoos.mybeautip.domain.video.code.VideoMaskType;
import com.jocoos.mybeautip.domain.video.code.VideoStatus;
import com.jocoos.mybeautip.domain.vod.code.VodSortField;
import com.jocoos.mybeautip.global.code.*;
import com.jocoos.mybeautip.global.config.restdoc.EnumDocs;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import com.jocoos.mybeautip.member.comment.Comment;
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
        Map<String, String> booleanType = getDocs(BooleanType.values());
        Map<String, String> deviceOs = getDocs(DeviceOs.values());
        Map<String, String> language = getDocs(Language.values());
        Map<String, String> telecom = getDocs(Telecom.values());
        Map<String, String> urlDirectory = getDocs(UrlDirectory.values());
        Map<String, String> fileOperationType = getDocs(FileOperationType.values());
        Map<String, String> fileType = getDocs(FileType.values());
        Map<String, String> sortField = getDocs(SortField.values());
        Map<String, String> countryCode = getDocs(CountryCode.values());

        // System
        Map<String, String> systemOptionType = getDocs(SystemOptionType.values());

        // Member
        Map<String, String> memberStatus = getDocs(MemberStatus.values());
        Map<String, String> influencerStatus = getDocs(InfluencerStatus.values());
        Map<String, String> skinType = getDocs(SkinType.values());
        Map<String, String> skinWorry = getDocs(SkinWorry.values());
        Map<String, String> grantType = getDocs(GrantType.values());
        Map<String, String> role = getDocs(Role.values());
        Map<String, String> memberActivityType = getDocs(MemberActivityType.values());

        // Broadcast
        Map<String, String> broadcastViewerType = getDocs(BroadcastViewerType.values());
        Map<String, String> broadcastViewerStatus = getDocs(BroadcastViewerStatus.values());
        Map<String, String> broadcastReportType = getDocs(BroadcastReportType.values());
        Map<String, String> broadcastStatus = getDocs(BroadcastStatus.values());
        Map<String, String> broadcastSortField = getDocs(BroadcastSortField.values());

        // Event
        Map<String, String> eventStatus = getDocs(EventStatus.values());
        Map<String, String> eventJoinStatus = getDocs(EventJoinStatus.values());
        Map<String, String> eventProductType = getDocs(EventProductType.values());
        Map<String, String> eventType = getDocs(EventType.values());

        // Community
        Map<String, String> communityCategoryType = getDocs(CommunityCategoryType.values());
        Map<String, String> communityStatus = getDocs(CommunityStatus.values());

        // Company
        Map<String, String> companyStatus = getDocs(CompanyStatus.values());
        Map<String, String> processPermission = getDocs(ProcessPermission.values());

        // Brand
        Map<String, String> brandStatus = getDocs(BrandStatus.values());

        // Delivery
        Map<String, String> deliveryCompanyStatus = getDocs(DeliveryCompanyStatus.values());
        Map<String, String> deliveryFeeSearchField = getDocs(DeliveryFeeSearchField.values());
        Map<String, String> deliveryFeeStatus = getDocs(DeliveryFeeStatus.values());
        Map<String, String> deliveryFeeType = getDocs(DeliveryFeeType.values());
        Map<String, String> deliveryMethod = getDocs(DeliveryMethod.values());
        Map<String, String> paymentOption = getDocs(PaymentOption.values());

        // Product

        // Store
        Map<String, String> storeCategoryStatus = getDocs(StoreCategoryStatus.values());

        // Video
        Map<String, String> videoCategoryType = getDocs(VideoCategoryType.values());
        Map<String, String> videoMaskType = getDocs(VideoMaskType.values());
        Map<String, String> videoCommentStatus = getDocs(Comment.CommentState.values());
        Map<String, String> videoStatus = getDocs(VideoStatus.values());

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

        // Popup Notice
        Map<String, String> popupNoticeLinkType = getDocs(PopupNoticeLinkType.values());

        // Term
        Map<String, String> termType = getDocs(TermType.values());

        // Search
        Map<String, String> searchType = getDocs(SearchType.values());

        // Scrap
        Map<String, String> scrapType = getDocs(ScrapType.values());

        // Operation
        Map<String, String> operationType = getDocs(OperationType.values());
        Map<String, String> operationTargetType = getDocs(OperationTargetType.values());

        // Notice
        Map<String, String> noticeStatus = getDocs(NoticeStatus.values());
        Map<String, String> noticeSort = getDocs(NoticeSort.values());

        // VOD
        Map<String, String> vodSortField = getDocs(VodSortField.values());

        // FFL
        Map<String, String> fflStreamKeyState = getDocs(FFLStreamKeyState.values());

        return ApiResponseDto.of(EnumDocs.builder()
                // Global
                        .booleanType(booleanType)
                        .deviceOs(deviceOs)
                        .language(language)
                        .telecom(telecom)
                        .urlDirectory(urlDirectory)
                        .fileOperationType(fileOperationType)
                        .fileType(fileType)
                        .sortField(sortField)
                        .countryCode(countryCode)
                // System
                        .systemOptionType(systemOptionType)
                // Member
                        .memberStatus(memberStatus)
                        .influencerStatus(influencerStatus)
                        .skinType(skinType)
                        .skinWorry(skinWorry)
                        .grantType(grantType)
                        .role(role)
                        .memberActivityType(memberActivityType)
                // Broadcast
                        .broadcastViewerType(broadcastViewerType)
                        .broadcastViewerStatus(broadcastViewerStatus)
                        .broadcastReportType(broadcastReportType)
                        .broadcastStatus(broadcastStatus)
                        .broadcastSortField(broadcastSortField)
                // Event
                        .eventStatus(eventStatus)
                        .eventJoinStatus(eventJoinStatus)
                        .eventProductType(eventProductType)
                        .eventType(eventType)
                // Community
                        .communityCategoryType(communityCategoryType)
                        .communityStatus(communityStatus)
                // Company
                        .companyStatus(companyStatus)
                        .processPermission(processPermission)
                // Brand
                        .brandStatus(brandStatus)
                // Delivery
                        .deliveryCompanyStatus(deliveryCompanyStatus)
                        .deliveryFeeSearchField(deliveryFeeSearchField)
                        .deliveryFeeStatus(deliveryFeeStatus)
                        .deliveryFeeType(deliveryFeeType)
                        .deliveryMethod(deliveryMethod)
                        .paymentOption(paymentOption)
                // Product
                // Store
                        .storeCategoryStatus(storeCategoryStatus)
                // Video
                        .videoCategoryType(videoCategoryType)
                        .videoMaskType(videoMaskType)
                        .videoCommentStatus(videoCommentStatus)
                        .videoStatus(videoStatus)
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
                // Popup Notice
                        .popupNoticeLinkType(popupNoticeLinkType)
                // Term
                        .termType(termType)
                // Search
                        .searchType(searchType)
                // Scrap
                        .scrapType(scrapType)
                // Operation
                        .operationTargetType(operationTargetType)
                        .operationType(operationType)
                // Notice
                        .noticeStatus(noticeStatus)
                        .noticeSort(noticeSort)
                // VOD
                        .vodSortField(vodSortField)
                // FFL
                        .fflStreamKeyState(fflStreamKeyState)
                .build()
        );
    }

    private Map<String, String> getDocs(CodeValue[] codeValues) {
        return Arrays.stream(codeValues)
                .collect(Collectors.toMap(CodeValue::getName, CodeValue::getDescription));
    }
}
