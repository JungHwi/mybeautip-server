package com.jocoos.mybeautip.domain.placard.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.PLACARD_LINK_TYPE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlacardControllerTest extends RestDocsTestSupport {

    @Test
    void getActivePlacards() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/placard"))
                .andExpect(status().isOk())
                .andDo(print());


        result.andDo(document("get_placards",
                responseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("플랜카드 목록"),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("플랜카드 타이틀"),
                        fieldWithPath("[].image_url").type(JsonFieldType.STRING).description("플랜카드 이미지 URL"),
                        fieldWithPath("[].color").type(JsonFieldType.STRING).description("플랜카드 색깔 정보"),
                        fieldWithPath("[].placard_link").type(JsonFieldType.OBJECT).description("플랜카드 클릭 시 연결 정보"),
                        fieldWithPath("[].placard_link.link_type").type(JsonFieldType.STRING).description(generateLinkCode((PLACARD_LINK_TYPE))),
                        fieldWithPath("[].placard_link.parameter").type(JsonFieldType.STRING).description("연결 정보 파라미터 (이벤트나 비디오 ID, null일 경우 해당 탭으로").optional()
                )));
    }

}
