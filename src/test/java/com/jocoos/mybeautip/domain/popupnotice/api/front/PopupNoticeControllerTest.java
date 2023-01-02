package com.jocoos.mybeautip.domain.popupnotice.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PopupNoticeControllerTest extends RestDocsTestSupport {

    @Test
    void getPopupNotices() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/popup/notice"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_popup_notices",
            responseFields(
                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("공지 팝업 목록").optional(),
                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("공지 팝업 아이디"),
                    fieldWithPath("[].image_url").type(JsonFieldType.STRING).description("공지 팝업 이미지 주소"),
                    fieldWithPath("[].link_type").type(JsonFieldType.STRING).description("공지 팝업 이미지 링크 타입").optional(),
                    fieldWithPath("[].parameter").type(JsonFieldType.STRING).description("공지 팝업 이미지 링크 파라미터").optional()
            )
        ));

    }

}
