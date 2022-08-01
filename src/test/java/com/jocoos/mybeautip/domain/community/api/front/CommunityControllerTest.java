package com.jocoos.mybeautip.domain.community.api.front;


import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommunityControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void writeCommunity() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("category_id", 3);
        map.put("title", "Test Title");
        map.put("contents", "Test Contents");
        map.put("files", null);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/1/community")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk());

        result.andDo(document("write_community",
                        requestFields(
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목. 수근수근에서만 필수").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("files").type(JsonFieldType.ARRAY).description("이미지 파일").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("file_url").type(JsonFieldType.ARRAY).description("파일 URL List").optional()
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getCommunities() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_communities",
                        requestParameters(
                                parameterWithName("category_id").description("카테고리 아이디").optional(),
                                parameterWithName("cursor").description("커서").optional().attributes(getDefault(MAX_LONG_STRING)),
                                parameterWithName("size").description("").optional().attributes(getDefault(20))
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("커뮤니티 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                                fieldWithPath("[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                                fieldWithPath("[].like_count").type(JsonFieldType.NUMBER).description("좋아요 카운트").optional(),
                                fieldWithPath("[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글 카운트").optional(),
                                fieldWithPath("[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("[].member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL"),
                                fieldWithPath("[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("[].category.title").type(JsonFieldType.STRING).description("카테고리 제목")
                        )
                )
        );
    }
}