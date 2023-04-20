package com.jocoos.mybeautip.domain.company.api.admin

import com.jocoos.mybeautip.domain.company.code.CompanyStatus
import com.jocoos.mybeautip.domain.company.code.ProcessPermission
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest
import com.jocoos.mybeautip.domain.company.persistence.domain.Company
import com.jocoos.mybeautip.domain.company.persistence.repository.CompanyRepository
import com.jocoos.mybeautip.domain.company.vo.CompanyPermissionVo
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COMPANY_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.PROCESS_PERMISSION
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeCompany
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminCompanyControllerTest(
    private val companyRepository: CompanyRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun create() {
        val permission = CompanyPermissionVo.builder()
            .createProduct(ProcessPermission.AUTO)
            .updateProduct(ProcessPermission.AUTO)
            .deleteProduct(ProcessPermission.ADMINISTRATOR)
            .build()

        val request = CreateCompanyRequest.builder()
            .name("회사")
            .status(CompanyStatus.ACTIVE)
            .salesFee(5.0f)
            .shippingFee(5.0f)
            .businessName("회에사아")
            .businessNumber("123-456-7890")
            .representativeName("김대표")
            .email("email@address.com")
            .phoneNumber("010-1234-5678")
            .businessType("업태")
            .businessItem("업종")
            .zipcode("012345")
            .address1("지구 극동아시아 한국")
            .address2("서울")
            .permission(permission)
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/admin/company")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_create_company",
                requestFields(
                    fieldWithPath("name").type(STRING).description("공급사명"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMPANY_STATUS)),
                    fieldWithPath("sales_fee").type(NUMBER).description("판매 수수료"),
                    fieldWithPath("shipping_fee").type(NUMBER).description("배송 수수료"),
                    fieldWithPath("business_name").type(STRING).description("상호명"),
                    fieldWithPath("business_number").type(STRING).description("사업자 번호"),
                    fieldWithPath("representative_name").type(STRING).description("대표자명"),
                    fieldWithPath("email").type(STRING).description("이메일"),
                    fieldWithPath("phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("business_type").type(STRING).description("업태"),
                    fieldWithPath("business_item").type(STRING).description("업종"),
                    fieldWithPath("zipcode").type(STRING).description("우편번호"),
                    fieldWithPath("address1").type(STRING).description("주소"),
                    fieldWithPath("address2").type(STRING).description("상세 주소"),
                    fieldWithPath("claim").type(OBJECT).description("claim 정보").optional(),
                    fieldWithPath("claim.customer_center_phone").type(STRING).description("고객센터 전화번호").optional(),
                    fieldWithPath("claim.zipcode").type(STRING).description("교환 / 환불 - 우편번호").optional(),
                    fieldWithPath("claim.address1").type(STRING).description("교환 / 환불 - 주소").optional(),
                    fieldWithPath("claim.address2").type(STRING).description("교환 / 환불 - 상세 주소").optional(),
                    fieldWithPath("permission").type(OBJECT).description("권한 정보").optional(),
                    fieldWithPath("permission.create_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("permission.update_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("permission.delete_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("accounts").type(ARRAY).description("계좌 List").optional(),
                    fieldWithPath("accounts.[].bank_name").type(STRING).description("은행명").optional(),
                    fieldWithPath("accounts.[].account_number").type(STRING).description("계좌번호").optional(),
                    fieldWithPath("accounts.[].owner_name").type(STRING).description("예금주명").optional(),

                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("공급사 ID"),
                    fieldWithPath("name").type(STRING).description("공급사명"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMPANY_STATUS)),
                    fieldWithPath("sales_fee").type(NUMBER).description("판매 수수료 (%)"),
                    fieldWithPath("shipping_fee").type(NUMBER).description("배송 수수료 (%)"),
                    fieldWithPath("business_name").type(STRING).description("상호명"),
                    fieldWithPath("business_number").type(STRING).description("사업자 번호"),
                    fieldWithPath("representative_name").type(STRING).description("대표자명"),
                    fieldWithPath("email").type(STRING).description("이메일"),
                    fieldWithPath("phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("business_type").type(STRING).description("업태"),
                    fieldWithPath("business_item").type(STRING).description("업종"),
                    fieldWithPath("zipcode").type(STRING).description("우편번호"),
                    fieldWithPath("address1").type(STRING).description("주소"),
                    fieldWithPath("address2").type(STRING).description("상세 주소"),
                    fieldWithPath("created_at").type(STRING).description("생성 시간").attributes(DocumentAttributeGenerator.getZonedDateFormat()),
                    fieldWithPath("claim").type(OBJECT).description("claim 정보").optional(),
                    fieldWithPath("claim.customer_center_phone").type(STRING).description("고객센터 전화번호").optional(),
                    fieldWithPath("claim.zipcode").type(STRING).description("교환 / 환불 - 우편번호").optional(),
                    fieldWithPath("claim.address1").type(STRING).description("교환 / 환불 - 주소").optional(),
                    fieldWithPath("claim.address2").type(STRING).description("교환 / 환불 - 상세 주소").optional(),
                    fieldWithPath("permission").type(OBJECT).description("권한 정보").optional(),
                    fieldWithPath("permission.create_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("permission.update_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("permission.delete_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("accounts").type(ARRAY).description("계좌 List").optional(),
                    fieldWithPath("accounts.[].id").type(NUMBER).description("계좌 아이디").optional(),
                    fieldWithPath("accounts.[].bank_name").type(STRING).description("은행명").optional(),
                    fieldWithPath("accounts.[].account_number").type(STRING).description("계좌번호").optional(),
                    fieldWithPath("accounts.[].owner_name").type(STRING).description("예금주명").optional(),
                )
            )
        )
    }

    @Test
    fun search() {
        // when & then
        saveCompany();

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/company")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_search_company",
                requestParameters(
                    parameterWithName("name").description("공급사명").optional(),
                    parameterWithName("status").description(generateLinkCode(COMPANY_STATUS) + "+ \n" +
                            "배열로 복수개 가능 ").optional(),
                    parameterWithName("page").description("페이지").optional(),
                    parameterWithName("size").description("페이지 사이즈").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("조회 Total Count"),
                    fieldWithPath("content").type(ARRAY).description("공급사 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("공급사 ID"),
                    fieldWithPath("content.[].name").type(STRING).description("공급사명"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMPANY_STATUS)),
                    fieldWithPath("content.[].sales_fee").type(NUMBER).description("판매 수수료 (%)"),
                    fieldWithPath("content.[].shipping_fee").type(NUMBER).description("배송 수수료 (%)"),
                    fieldWithPath("content.[].permission").type(OBJECT).description("권한 정보").optional(),
                    fieldWithPath("content.[].permission.create_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("content.[].permission.update_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("content.[].permission.delete_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("content.[].created_at").type(STRING).description("생성 시간").attributes(DocumentAttributeGenerator.getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun get() {
        val company = saveCompany();

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/company/{companyId}", company.id)
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_company",
                pathParameters(
                    parameterWithName("companyId").description("공급사 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("공급사 ID"),
                    fieldWithPath("name").type(STRING).description("공급사명"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMPANY_STATUS)),
                    fieldWithPath("sales_fee").type(NUMBER).description("판매 수수료 (%)"),
                    fieldWithPath("shipping_fee").type(NUMBER).description("배송 수수료 (%)"),
                    fieldWithPath("business_name").type(STRING).description("상호명"),
                    fieldWithPath("business_number").type(STRING).description("사업자 번호"),
                    fieldWithPath("representative_name").type(STRING).description("대표자명"),
                    fieldWithPath("email").type(STRING).description("이메일"),
                    fieldWithPath("phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("business_type").type(STRING).description("업태"),
                    fieldWithPath("business_item").type(STRING).description("업종"),
                    fieldWithPath("zipcode").type(STRING).description("우편번호"),
                    fieldWithPath("address1").type(STRING).description("주소"),
                    fieldWithPath("address2").type(STRING).description("상세 주소"),
                    fieldWithPath("created_at").type(STRING).description("생성 시간").attributes(DocumentAttributeGenerator.getZonedDateFormat()),
                    fieldWithPath("claim").type(OBJECT).description("claim 정보").optional(),
                    fieldWithPath("claim.customer_center_phone").type(STRING).description("고객센터 전화번호").optional(),
                    fieldWithPath("claim.zipcode").type(STRING).description("교환 / 환불 - 우편번호").optional(),
                    fieldWithPath("claim.address1").type(STRING).description("교환 / 환불 - 주소").optional(),
                    fieldWithPath("claim.address2").type(STRING).description("교환 / 환불 - 상세 주소").optional(),
                    fieldWithPath("permission").type(OBJECT).description("권한 정보").optional(),
                    fieldWithPath("permission.create_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("permission.update_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("permission.delete_product").type(STRING).description(generateLinkCode(PROCESS_PERMISSION)),
                    fieldWithPath("accounts").type(ARRAY).description("계좌 List").optional(),
                    fieldWithPath("accounts.[].id").type(NUMBER).description("계좌 아이디").optional(),
                    fieldWithPath("accounts.[].bank_name").type(STRING).description("은행명").optional(),
                    fieldWithPath("accounts.[].account_number").type(STRING).description("계좌번호").optional(),
                    fieldWithPath("accounts.[].owner_name").type(STRING).description("예금주명").optional(),
                )
            )
        )
    }

    fun saveCompany(): Company {
        return companyRepository.save(makeCompany())
    }
}