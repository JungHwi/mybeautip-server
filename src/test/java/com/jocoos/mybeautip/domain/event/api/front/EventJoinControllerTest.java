package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

class EventJoinControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void joinEvent() throws Exception {
//        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
//                        .post("/api/1/event/join/{eventId}", 4)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//        result.andDo(document("event_join",
//                        pathParameters(
//                                parameterWithName("eventId").description("Event ID")
//                        ),
//                        responseFields(
//                                fieldWithPath("result").description("이벤트 참여 결과")
//                        )
//                    )
//                );
    }
}
