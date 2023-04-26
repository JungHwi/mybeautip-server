package com.jocoos.mybeautip.domain.brand.api.admin

import com.jocoos.mybeautip.domain.brand.code.BrandStatus
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest
import com.jocoos.mybeautip.domain.brand.persistence.repository.BrandRepository
import com.jocoos.mybeautip.domain.company.persistence.domain.Company
import com.jocoos.mybeautip.domain.company.persistence.repository.CompanyRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.BRAND_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COMPANY_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeCompany
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class AdminBrandControllerTest(
    private val brandRepository: BrandRepository,
    private val companyRepository: CompanyRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun create() {
        val company = saveCompany()
        val request = CreateBrandRequest.builder()
            .name("브랜드")
            .status(BrandStatus.ACTIVE)
            .description("브랜드 설명")
            .companyId(company.id)
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/admin/brand")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_create_brand",
                requestFields(
                    fieldWithPath("company_id").type(NUMBER).description("공급사 아이디"),
                    fieldWithPath("name").type(STRING).description("브랜드명"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(BRAND_STATUS)),
                    fieldWithPath("description").type(STRING).description("브랜드 설명").optional(),
                    ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("브랜드 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(BRAND_STATUS)),
                    fieldWithPath("name").type(STRING).description("브랜드명"),
                    fieldWithPath("code").type(STRING).description("브랜드 코드"),
                    fieldWithPath("description").type(STRING).description("브랜드 설명"),
                    fieldWithPath("company").type(OBJECT).description("공급사 정보"),
                    fieldWithPath("company.id").type(NUMBER).description("공급사 아이디"),
                    fieldWithPath("company.name").type(STRING).description("공급사명"),
                    fieldWithPath("company.status").type(STRING).description(generateLinkCode(COMPANY_STATUS)),
                )
            )
        )
    }


    fun saveCompany(): Company {
        return companyRepository.save(makeCompany())
    }
}