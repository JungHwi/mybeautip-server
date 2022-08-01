package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventJoinControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void joinEvent() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "breeze");

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/1/event/join/{eventId}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk());

        result.andDo(document("event_join",
                        pathParameters(
                                parameterWithName("eventId").description("Event ID")
                        ),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)).optional(),
                                fieldWithPath("cursor").type(JsonFieldType.NUMBER).description("커서").attributes(getDefault(20)).optional(),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성일").optional().attributes(getZonedDateFormat())
                        ),
                        responseFields(
                                fieldWithPath("result").description("이벤트 참여 결과")
                        )
                    )
                );
    }
}