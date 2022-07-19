package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class EventJoinControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void joinEvent() throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/1/event/join/{eventId}", 3)
        )
                .andExpect(status().isOk())
                .andDo(document("event-join",
                        pathParameters(
                                parameterWithName("eventId").description("Event ID")
                        ),
                        responseFields(
                                fieldWithPath("result").description("이벤트 참여 결과")
                        )
                    )
                );
    }
}