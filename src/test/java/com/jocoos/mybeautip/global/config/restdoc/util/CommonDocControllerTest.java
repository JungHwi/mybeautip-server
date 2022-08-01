package com.jocoos.mybeautip.global.config.restdoc.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jocoos.mybeautip.global.config.restdoc.EnumDocs;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
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

public class CommonDocControllerTest extends RestDocsTestSupport {

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
                        // Member
                        customResponseFields("custom-response", beneathPath("data.member_status").withSubsectionId("member_status"),
                                attributes(key("title").value("MemberStatus")),
                                enumConvertFieldDescriptor((enumDocs.getMemberStatus()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.skin_type").withSubsectionId("skin_type"),
                                attributes(key("title").value("SkinType")),
                                enumConvertFieldDescriptor((enumDocs.getSkinType()))
                        ),
                        customResponseFields("custom-response", beneathPath("data.skin_worry").withSubsectionId("skin_worry"),
                                attributes(key("title").value("SkinWorry")),
                                enumConvertFieldDescriptor((enumDocs.getSkinWorry()))
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
