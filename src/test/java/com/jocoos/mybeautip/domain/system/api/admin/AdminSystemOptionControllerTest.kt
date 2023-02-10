package com.jocoos.mybeautip.domain.system.api.admin

import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption
import com.jocoos.mybeautip.domain.system.persistence.repository.SystemOptionRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.SYSTEM_OPTION_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeSystemOption
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class AdminSystemOptionControllerTest(private val systemOptionRepository: SystemOptionRepository) : RestDocsIntegrationTestSupport() {

    @Test
    fun updateSystemOption() {
        val systemOption: SystemOption = systemOptionRepository.save(makeSystemOption())

        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                RestDocumentationRequestBuilders.patch("/admin/system/{id}", systemOption.id)
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        result.andDo(
            MockMvcRestDocumentation.document(
                "admin_edit_system_option",
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description(generateLinkCode(SYSTEM_OPTION_TYPE))
                ),
                PayloadDocumentation.requestFields(
                    PayloadDocumentation.fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("시스템 옵션 설정 값")
                ),
                PayloadDocumentation.responseFields(
                    PayloadDocumentation.fieldWithPath("id").type(JsonFieldType.STRING).description(generateLinkCode(SYSTEM_OPTION_TYPE)),
                    PayloadDocumentation.fieldWithPath("value").type(JsonFieldType.BOOLEAN).description("시스템 옵션 설정 값"),
                )
            )
        )
    }
}