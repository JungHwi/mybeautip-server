package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScrapControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getScrap() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/my/scrap")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community_scraps",
                        requestParameters(
                                parameterWithName("cursor").description("커서").optional(),
                                parameterWithName("size").description("조회 갯수").optional().attributes(getDefault(20))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보").attributes(getZonedDateMilliFormat()),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 스크랩 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("커뮤니티 ID"),
                                fieldWithPath("content.[].type").type(JsonFieldType.STRING).description("스크랩 타"),
                                fieldWithPath("content.[].scrap_id").type(JsonFieldType.NUMBER).description("스크랩 ID"),
                                fieldWithPath("content.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                                fieldWithPath("content.[].file_url").type(JsonFieldType.ARRAY).description("파일 URL").optional(),
                                fieldWithPath("content.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("content.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                                fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("content.[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("content.[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("content.[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("content.[].relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                                fieldWithPath("content.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                                fieldWithPath("content.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                                fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("content.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("content.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("content.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("content.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

}
