package com.jocoos.mybeautip.domain.video.api.admin;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VIDEO_CATEGORY_TYPE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminVideoCategoryControllerTest extends RestDocsTestSupport {

    @Test
    void getCategories() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/video/category"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_video_category",
                responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("비디오 카테고리 ID"),
                        fieldWithPath("[].type").type(JsonFieldType.STRING).description(generateLinkCode(VIDEO_CATEGORY_TYPE)),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("비디오 카테고리 이름")
                )));
    }

}
