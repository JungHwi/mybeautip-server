package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityCategoryControllerTest extends RestDocsTestSupport {

    @Test
    void getCommunityCategories() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_community_categories",
                        requestParameters(
                                parameterWithName("type").optional().attributes(getDefault(CommunityCategoryType.GENERAL))
                                        .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE) + "+ \n" +
                                        "일반 게시판의 경우에는 [GENERAL] + \n" +
                                        "익명 게시판의 경우에는 [ANONYMOUS]")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("커뮤니티 카테고리 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("[].type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("[].hint").type(JsonFieldType.STRING).description("힌트")
                        )
                )
        );
    }

    @Test
    void getCommunityCategory() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/category/{category_id}", 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_community_category",
                        pathParameters(
                                parameterWithName("category_id").description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("hint").type(JsonFieldType.STRING).description("힌트")
                        )
                )
        );
    }
}