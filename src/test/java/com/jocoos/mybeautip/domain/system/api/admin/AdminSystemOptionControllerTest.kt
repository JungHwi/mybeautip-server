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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminSystemOptionControllerTest(private val systemOptionRepository: SystemOptionRepository) : RestDocsIntegrationTestSupport() {

    val systemOption: SystemOption = systemOptionRepository.save(makeSystemOption())

    @Test
    fun getSystemOption() {
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/system/{id}", systemOption.id)
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_system_option",
                pathParameters(
                    parameterWithName("id").description(generateLinkCode(SYSTEM_OPTION_TYPE))
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description(generateLinkCode(SYSTEM_OPTION_TYPE)),
                    fieldWithPath("value").type(JsonFieldType.BOOLEAN).description("시스템 옵션 설정 값"),
                )
            )
        )
    }

    @Test
    fun updateSystemOption() {
        val systemOption: SystemOption = systemOptionRepository.save(makeSystemOption())

        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/system/{id}", systemOption.id)
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_system_option",
                pathParameters(
                    parameterWithName("id").description(generateLinkCode(SYSTEM_OPTION_TYPE))
                ),
                requestFields(
                    fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("시스템 옵션 설정 값")
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description(generateLinkCode(SYSTEM_OPTION_TYPE)),
                    fieldWithPath("value").type(JsonFieldType.BOOLEAN).description("시스템 옵션 설정 값"),
                )
            )
        )
    }
}