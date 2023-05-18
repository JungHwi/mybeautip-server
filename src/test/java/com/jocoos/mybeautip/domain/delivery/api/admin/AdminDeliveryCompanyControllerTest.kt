package com.jocoos.mybeautip.domain.delivery.api.admin

import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryCompany
import com.jocoos.mybeautip.domain.delivery.service.dao.DeliveryCompanyDao
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.DELIVERY_COMPANY_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeCreateDeliveryCompanyRequest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminDeliveryCompanyControllerTest (
    private val dao: DeliveryCompanyDao
) : RestDocsIntegrationTestSupport() {

    @Test
    fun create() {
        val request = makeCreateDeliveryCompanyRequest()

        val result: ResultActions = mockMvc
            .perform(
                post("/admin/delivery/company")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_create_delivery_company",
                requestFields(
                    fieldWithPath("status").type(STRING).description(generateLinkCode(DELIVERY_COMPANY_STATUS)),
                    fieldWithPath("name").type(STRING).description("배송업체명"),
                    fieldWithPath("url").type(STRING).description("배송조회 API URL"),
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("배송업체 ID"),
                    fieldWithPath("code").type(STRING).description("배송업체 코드"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(DELIVERY_COMPANY_STATUS)),
                    fieldWithPath("name").type(STRING).description("배송업체명"),
                    fieldWithPath("url").type(STRING).description("배송조회 API URL")
                )
            )
        )
    }

    @Test
    fun search() {
        saveDeliveryCompany()

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/delivery/company")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_search_delivery_company",
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("배송업체 목록"),
                    fieldWithPath("[].id").type(NUMBER).description("배송업체 ID"),
                    fieldWithPath("[].code").type(STRING).description("배송업체 코드"),
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(DELIVERY_COMPANY_STATUS)),
                    fieldWithPath("[].name").type(STRING).description("배송업체명"),
                    fieldWithPath("[].url").type(STRING).description("배송조회 API URL")
                )
            )
        )
    }

    fun saveDeliveryCompany(
    ): DeliveryCompany {
        return dao.create(makeCreateDeliveryCompanyRequest())
    }
}