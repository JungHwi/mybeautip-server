package com.jocoos.mybeautip.domain.healthcheck.api.front

import com.jocoos.mybeautip.devices.HealthCheckRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.testutil.fixture.makeHealthCheck
import com.jocoos.mybeautip.testutil.fixture.makeMember
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class HealthCheckControllerTest(
    private val healthCheckRepository : HealthCheckRepository)
: RestDocsIntegrationTestSupport() {

    @Test
    fun healthCheck() {
        healthCheckRepository.save(makeHealthCheck(makeMember()))

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/healthcheck")
                    .header(HttpHeaders.AUTHORIZATION, requestUserToken)
                    .param("device_os", "android")
                    .param("app_version", "0.0.1")
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "health_check",
                requestParameters(
                    parameterWithName("device_os").description("OS. android / ios"),
                    parameterWithName("app_version").description("앱 버전"),
                    parameterWithName("lang").attributes(getDefault("ko")).description("언어").optional(),
                ),
                responseFields(
                    fieldWithPath("content.[]").type(JsonFieldType.ARRAY).description("헬스체크 목록").optional(),
                    fieldWithPath("content.[].type").type(JsonFieldType.STRING).description("헬스체크 구분. 1 - 강제 업데이트. 2 - 업데이트 권고. 3 - 앱 강제 종료"),
                    fieldWithPath("content.[].message").type(JsonFieldType.STRING).description("헬스체크 메세지"),
                )
            )
        )
    }
}