package com.jocoos.mybeautip.global.config.restdoc.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jocoos.mybeautip.global.config.restdoc.EnumDocs;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonDocControllerTest extends RestDocsIntegrationTestSupport {

    @Test
    public void enums() throws Exception {
        ResultActions result = this.mockMvc.perform(
                get("/test/enums")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        MvcResult mvcResult = result.andReturn();
        EnumDocs enumDocs = getData(mvcResult);

        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        // Global
                        customResponseFields("custom-response", beneathPath("data.boolean_type").withSubsectionId("boolean_type"),
                                attributes(key("title").value("BooleanType")),
                                enumConvertFieldDescriptor((enumDocs.getBooleanType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.device_os").withSubsectionId("device_os"),
                                attributes(key("title").value("DeviceOs")),
                                enumConvertFieldDescriptor((enumDocs.getDeviceOs()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.language").withSubsectionId("language"),
                                attributes(key("title").value("Language")),
                                enumConvertFieldDescriptor((enumDocs.getLanguage()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.telecom").withSubsectionId("telecom"),
                                attributes(key("title").value("Telecom")),
                                enumConvertFieldDescriptor((enumDocs.getTelecom()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.url_directory").withSubsectionId("url_directory"),
                                attributes(key("title").value("UrlDirectory")),
                                enumConvertFieldDescriptor((enumDocs.getUrlDirectory()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.file_operation_type").withSubsectionId("file_operation_type"),
                                attributes(key("title").value("FileOperationType")),
                                enumConvertFieldDescriptor((enumDocs.getFileOperationType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.file_type").withSubsectionId("file_type"),
                                attributes(key("title").value("FileOperationType")),
                                enumConvertFieldDescriptor((enumDocs.getFileType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.sort_field").withSubsectionId("sort_field"),
                                attributes(key("title").value("SortField")),
                                enumConvertFieldDescriptor((enumDocs.getSortField()))
                        ),
                        // System
                        customResponseFields("custom-response", beneathPath("data.system_option_type").withSubsectionId("system_option_type"),
                                attributes(key("title").value("SystemOptionType")),
                                enumConvertFieldDescriptor((enumDocs.getSystemOptionType()))
                        ),
                        // Member
                        customResponseFields("custom-response", beneathPath("data.member_status").withSubsectionId("member_status"),
                                attributes(key("title").value("MemberStatus")),
                                enumConvertFieldDescriptor((enumDocs.getMemberStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.influencer_status").withSubsectionId("influencer_status"),
                                attributes(key("title").value("InfluencerStatus")),
                                enumConvertFieldDescriptor((enumDocs.getInfluencerStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.skin_type").withSubsectionId("skin_type"),
                                attributes(key("title").value("SkinType")),
                                enumConvertFieldDescriptor((enumDocs.getSkinType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.skin_worry").withSubsectionId("skin_worry"),
                                attributes(key("title").value("SkinWorry")),
                                enumConvertFieldDescriptor((enumDocs.getSkinWorry()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.grant_type").withSubsectionId("grant_type"),
                                attributes(key("title").value("GrantType")),
                                enumConvertFieldDescriptor((enumDocs.getGrantType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.role").withSubsectionId("role"),
                                attributes(key("title").value("Role")),
                                enumConvertFieldDescriptor((enumDocs.getRole()))
                        ),
                        // Broadcast
                        customResponseFields("custom-response", beneathPath("data.broadcast_viewer_type").withSubsectionId("broadcast_viewer_type"),
                                attributes(key("title").value("BroadcastViewerType")),
                                enumConvertFieldDescriptor((enumDocs.getBroadcastViewerType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.broadcast_viewer_status").withSubsectionId("broadcast_viewer_status"),
                                attributes(key("title").value("BroadcastViewerStatus")),
                                enumConvertFieldDescriptor((enumDocs.getBroadcastViewerStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.broadcast_status").withSubsectionId("broadcast_status"),
                                attributes(key("title").value("BroadcastStatus")),
                                enumConvertFieldDescriptor((enumDocs.getBroadcastStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.broadcast_sort_field").withSubsectionId("broadcast_sort_field"),
                                attributes(key("title").value("BroadcastSortField")),
                                enumConvertFieldDescriptor((enumDocs.getBroadcastSortField()))
                        ),
                        // Event
                        customResponseFields("custom-response", beneathPath("data.event_status").withSubsectionId("event_status"),
                                attributes(key("title").value("EventStatus")),
                                enumConvertFieldDescriptor((enumDocs.getEventStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.event_join_status").withSubsectionId("event_join_status"),
                                attributes(key("title").value("EventJoinStatus")),
                                enumConvertFieldDescriptor((enumDocs.getEventJoinStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.event_product_type").withSubsectionId("event_product_type"),
                                attributes(key("title").value("EventProductType")),
                                enumConvertFieldDescriptor((enumDocs.getEventProductType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.event_type").withSubsectionId("event_type"),
                                attributes(key("title").value("EventType")),
                                enumConvertFieldDescriptor((enumDocs.getEventType()))
                        ),
                        // Community
                        customResponseFields("custom-response", beneathPath("data.community_category_type").withSubsectionId("community_category_type"),
                                attributes(key("title").value("CommunityCategoryType")),
                                enumConvertFieldDescriptor((enumDocs.getCommunityCategoryType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.community_status").withSubsectionId("community_status"),
                                attributes(key("title").value("CommunityStatus")),
                                enumConvertFieldDescriptor((enumDocs.getCommunityStatus()))
                        ),
                        // Video
                        customResponseFields("custom-response", beneathPath("data.video_category_type").withSubsectionId("video_category_type"),
                                attributes(key("title").value("VideoCategoryType")),
                                enumConvertFieldDescriptor((enumDocs.getVideoCategoryType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.video_mask_type").withSubsectionId("video_mask_type"),
                                attributes(key("title").value("VideoMaskType")),
                                enumConvertFieldDescriptor((enumDocs.getVideoMaskType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.video_comment_status").withSubsectionId("video_comment_status"),
                                attributes(key("title").value("VideoCommentStatus")),
                                enumConvertFieldDescriptor((enumDocs.getVideoCommentStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.video_status").withSubsectionId("video_status"),
                                attributes(key("title").value("VideoStatus")),
                                enumConvertFieldDescriptor((enumDocs.getVideoStatus()))
                        ),
                        // Notification
                        customResponseFields("custom-response", beneathPath("data.notification_link_type").withSubsectionId("notification_link_type"),
                                attributes(key("title").value("NotificationLinkType")),
                                enumConvertFieldDescriptor((enumDocs.getNotificationLinkType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.message_type").withSubsectionId("message_type"),
                                attributes(key("title").value("MessageType")),
                                enumConvertFieldDescriptor((enumDocs.getMessageType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.notification_argument").withSubsectionId("notification_argument"),
                                attributes(key("title").value("NotificationArgument")),
                                enumConvertFieldDescriptor((enumDocs.getNotificationArgument()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.notification_platform").withSubsectionId("notification_platform"),
                                attributes(key("title").value("NotificationPlatform")),
                                enumConvertFieldDescriptor((enumDocs.getNotificationPlatform()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.notification_status").withSubsectionId("notification_status"),
                                attributes(key("title").value("NotificationStatus")),
                                enumConvertFieldDescriptor((enumDocs.getNotificationStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.send_type").withSubsectionId("send_type"),
                                attributes(key("title").value("SendType")),
                                enumConvertFieldDescriptor((enumDocs.getSendType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.template_type").withSubsectionId("template_type"),
                                attributes(key("title").value("TemplateType")),
                                enumConvertFieldDescriptor((enumDocs.getTemplateType()))
                        ),
                        // Placard
                        customResponseFields("custom-response", beneathPath("data.placard_link_type").withSubsectionId("placard_link_type"),
                                attributes(key("title").value("PlacardLinkType")),
                                enumConvertFieldDescriptor((enumDocs.getPlacardLinkType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.placard_status").withSubsectionId("placard_status"),
                                attributes(key("title").value("PlacardStatus")),
                                enumConvertFieldDescriptor((enumDocs.getPlacardStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.placard_tab_type").withSubsectionId("placard_tab_type"),
                                attributes(key("title").value("PlacardTabType")),
                                enumConvertFieldDescriptor((enumDocs.getPlacardTabType()))
                        ),
                        // Point
                        customResponseFields("custom-response", beneathPath("data.point_status").withSubsectionId("point_status"),
                                attributes(key("title").value("PointStatus")),
                                enumConvertFieldDescriptor((enumDocs.getPointStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.point_status_group").withSubsectionId("point_status_group"),
                                attributes(key("title").value("PointStatusGroup")),
                                enumConvertFieldDescriptor((enumDocs.getPointStatusGroup()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.use_point_service").withSubsectionId("use_point_service"),
                                attributes(key("title").value("UsePointService")),
                                enumConvertFieldDescriptor((enumDocs.getUsePointService()))
                        ),
                        // Popup
                        customResponseFields("custom-response", beneathPath("data.button_link_type").withSubsectionId("button_link_type"),
                                attributes(key("title").value("ButtonLinkType")),
                                enumConvertFieldDescriptor((enumDocs.getButtonLinkType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.popup_display_type").withSubsectionId("popup_display_type"),
                                attributes(key("title").value("PopupDisplayType")),
                                enumConvertFieldDescriptor((enumDocs.getPopupDisplayType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.popup_status").withSubsectionId("popup_status"),
                                attributes(key("title").value("PopupStatus")),
                                enumConvertFieldDescriptor((enumDocs.getPopupStatus()))
                        ),
                        // Popup Notice
                        customResponseFields("custom-response", beneathPath("data.popup_notice_link_type").withSubsectionId("popup_notice_link_type"),
                                attributes(key("title").value("PopupNoticeLinkType")),
                                enumConvertFieldDescriptor((enumDocs.getPopupNoticeLinkType()))
                        ),
                        // TERM
                        customResponseFields("custom-response", beneathPath("data.term_type").withSubsectionId("term_type"),
                                attributes(key("title").value("TermType")),
                                enumConvertFieldDescriptor((enumDocs.getTermType()))
                        ),
                        // SEARCH
                        customResponseFields("custom-response", beneathPath("data.search_type").withSubsectionId("search_type"),
                                attributes(key("title").value("SearchType")),
                                enumConvertFieldDescriptor((enumDocs.getSearchType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.placard_link_type").withSubsectionId("placard_link_type"),
                                attributes(key("title").value("PlacardLinkType")),
                                enumConvertFieldDescriptor((enumDocs.getPlacardLinkType()))),
                        // Operation
                        customResponseFields("custom-response", beneathPath("data.operation_type").withSubsectionId("operation_type"),
                                attributes(key("title").value("OperationType")),
                                enumConvertFieldDescriptor((enumDocs.getOperationType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.operation_target_type").withSubsectionId("operation_target_type"),
                                attributes(key("title").value("OperationTargetType")),
                                enumConvertFieldDescriptor((enumDocs.getOperationTargetType()))
                        ),
                        // Notice
                        customResponseFields("custom-response", beneathPath("data.notice_status").withSubsectionId("notice_status"),
                                attributes(key("title").value("NoticeStatus")),
                                enumConvertFieldDescriptor((enumDocs.getNoticeStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.notice_sort").withSubsectionId("notice_sort"),
                                attributes(key("title").value("NoticeSort")),
                                enumConvertFieldDescriptor((enumDocs.getNoticeSort()))
                        ),
                        // Vod
                        customResponseFields("custom-response", beneathPath("data.vod_sort_field").withSubsectionId("vod_sort_field"),
                                attributes(key("title").value("VodSortField")),
                                enumConvertFieldDescriptor((enumDocs.getVodSortField()))
                        )
                ));
    }

    public static CustomResponseFieldsSnippet customResponseFields(String type,
                                                                   PayloadSubsectionExtractor<?> subsectionExtractor,
                                                                   Map<String, Object> attributes, FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes, true);
    }

    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);
    }

    private EnumDocs getData(MvcResult result) throws IOException {
        ApiResponseDto<EnumDocs> apiResponseDto = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(),
                        new TypeReference<ApiResponseDto<EnumDocs>>() {}
                );
        return apiResponseDto.getData();
    }
}
