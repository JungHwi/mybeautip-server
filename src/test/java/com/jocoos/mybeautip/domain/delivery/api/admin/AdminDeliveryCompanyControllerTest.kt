package com.jocoos.mybeautip.domain.delivery.api.admin

import com.jocoos.mybeautip.domain.delivery.code.DeliveryCompanyStatus
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
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

    @Test
    fun edit() {
        val deliveryCompany = saveDeliveryCompany()
        val request = makeCreateDeliveryCompanyRequest()

        val result: ResultActions = mockMvc
            .perform(
                put("/admin/delivery/company/{deliveryCompanyId}", deliveryCompany.id)
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_delivery_company",
                pathParameters(
                    parameterWithName("deliveryCompanyId").description("배송업체 ID")
                ),
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
    fun delete() {
        val deliveryCompany = saveDeliveryCompany(status = DeliveryCompanyStatus.INACTIVE);

        val result: ResultActions = mockMvc
            .perform(
                delete("/admin/delivery/company/{deliveryCompanyId}", deliveryCompany.id)
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent)
            .andDo(print())

        result.andDo(
            document(
                "admin_delete_delivery_company",
                pathParameters(
                    parameterWithName("deliveryCompanyId").description("배송업체 ID")
                )
            )
        )
    }


    fun saveDeliveryCompany(
        status: DeliveryCompanyStatus? = DeliveryCompanyStatus.ACTIVE
    ): DeliveryCompany {
        return dao.create(makeCreateDeliveryCompanyRequest(status))
    }
}