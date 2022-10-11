package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityScrapControllerTest extends RestDocsTestSupport {


    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void scrapTest() throws Exception {
        BooleanDto request = new BooleanDto(true);
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/scrap", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("scrap_community",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("스크랩 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("스크랩 ID"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.SCRAP_TYPE)),
                        fieldWithPath("community_id").type(JsonFieldType.NUMBER).description("커뮤니티 ID"),
                        fieldWithPath("is_scrap").type(JsonFieldType.BOOLEAN).description("스크랩 여부"),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("생성일자")
                )));
    }

}
