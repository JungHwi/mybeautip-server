package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityCategoryControllerTest extends RestDocsTestSupport {

    @Test
    void getCommunityCategory() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_community_category",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("커뮤니티 카테고리 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("[].type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목")
                        )
                )
        );
    }
}