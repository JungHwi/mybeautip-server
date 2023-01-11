package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport;

class AuthControllerTest extends RestDocsIntegrationTestSupport {

//    @Test
//    void getTokenForApp() throws Exception {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "naver");
//        params.add("social_id", "1");
//
//        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
//                        .post("/api/1/token")
//                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
//                        .header("Authorization", "Basic bXliZWF1dGlwLWFuZHJvaWQ6YWtkbHFieGxxZGtzZW1maGRsZW0=")
//                        .params(params))
//                .andExpect(status().isOk());
//
//        result.andDo(document("get_token_for_app",
//                        requestParameters(
//                                parameterWithName("grant_type").description("소셜 서비스. naver / kakao / apple / facebook"),
//                                parameterWithName("social_id").description("소셜 서비스 ID")
//                        ),
//                        responseFields(
//                                fieldWithPath("access_token").type(JsonFieldType.STRING).description("액세스 토큰"),
//                                fieldWithPath("token_type").type(JsonFieldType.STRING).description("bearer"),
//                                fieldWithPath("refresh_token").type(JsonFieldType.STRING).description("갱신 토큰"),
//                                fieldWithPath("expires_in").type(JsonFieldType.NUMBER).description("유효기간(분)"),
//                                fieldWithPath("scope").type(JsonFieldType.STRING).description("권한"),
//                                fieldWithPath("jti").type(JsonFieldType.STRING).description("토큰 아이디")
//                        )
//                )
//        );
//    }
}
