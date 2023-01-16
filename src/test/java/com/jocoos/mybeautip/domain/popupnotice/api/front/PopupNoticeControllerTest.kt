package com.jocoos.mybeautip.domain.popupnotice.api.front

import com.jocoos.mybeautip.domain.popupnotice.persistence.repository.PopupNoticeRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.POPUP_NOTICE_LINK_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makePopupNotice
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PopupNoticeControllerTest(
    private val popupNoticeRepository: PopupNoticeRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getPopupNotices() {

        // given
        popupNoticeRepository.save(makePopupNotice())

        // when & then
        val result: ResultActions = mockMvc
            .perform(
            get("/api/1/popup/notice")
                .header(AUTHORIZATION, requestUserToken)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_popup_notices",
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("공지 팝업 목록"),
                    fieldWithPath("[].id").type(NUMBER).description("공지 팝업 아이디"),
                    fieldWithPath("[].image_url").type(STRING).description("공지 팝업 이미지 주소"),
                    fieldWithPath("[].link_type").type(STRING).description(generateLinkCode(POPUP_NOTICE_LINK_TYPE)).optional(),
                    fieldWithPath("[].parameter").type(STRING).description("공지 팝업 이미지 링크 파라미터").optional()
                )
            )
        )
    }
}
