package com.jocoos.mybeautip.domain.zzz_test.api;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TestControllerTest extends RestDocsIntegrationTestSupport {

    @Test
    @Transactional
    void toDormantTest() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/test/member/{memberId}/dormant", requestUser.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("test_to_dormant_member",
                pathParameters(
                        parameterWithName("memberId").description("Member ID")
                )
        ));
    }

    @Test
    @Transactional
    void toActiveTest() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/test/member/{memberId}/active", requestUser.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("test_to_active_member",
                pathParameters(
                        parameterWithName("memberId").description("Member ID")
                )
        ));
    }
}
