package com.jocoos.mybeautip.domain.video.api.front;

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

class VideoCategoryControllerTest extends RestDocsTestSupport {

    @Test
    void getVideoCategories() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/video/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_video_categories",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("비디오 카테고리 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("[].type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.VIDEO_CATEGORY_TYPE)),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("[].shape_url").type(JsonFieldType.STRING).description("Shape URL").optional(),
                                fieldWithPath("[].mask_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.VIDEO_MASK_TYPE)).optional()
                        )
                )
        );
    }
}
