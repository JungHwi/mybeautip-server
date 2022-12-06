package com.jocoos.mybeautip.domain.community.api.admin;

import com.jocoos.mybeautip.domain.community.dto.PatchCommunityRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.NORMAL;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCommunityControllerTest extends RestDocsTestSupport {


    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Test
    void writeCommunity() throws Exception {
        WriteCommunityRequest request = WriteCommunityRequest.builder()
                .status(NORMAL)
                .categoryId(5L)
                .title("Mock Title")
                .contents("Mock Contents")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/admin/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print());

        result.andDo(document("admin_write_community",
                        requestFields(
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("event_id").type(JsonFieldType.NUMBER).description("이벤트 아이디. 써봐줄게 일때 관련된 이벤트 아이디.").optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("title").type(JsonFieldType.NUMBER).description("제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("image_urls").type(JsonFieldType.ARRAY).description("이미지 URL").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 ID"),
                                fieldWithPath("is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부").optional(),
                                fieldWithPath("is_top_fix").type(JsonFieldType.BOOLEAN).description("상단 고정 여부").optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("event_title").type(JsonFieldType.STRING).description("이벤트 제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용").optional(),
                                fieldWithPath("['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                                fieldWithPath("votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                                fieldWithPath("votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                                fieldWithPath("votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                                fieldWithPath("votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                                fieldWithPath("votes.[].is_voted").type(JsonFieldType.BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                                fieldWithPath("view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                                fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보.").optional(),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("category.title").type(JsonFieldType.STRING).description("카테고리 제목")
                        )
                )
        );
    }

    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Test
    void editCommunity() throws Exception {
        PatchCommunityRequest request = PatchCommunityRequest
                .builder()
                .contents(JsonNullable.of("수정"))
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/community/{community_id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_edit_community",
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목. 속닥속닥에서만 필수").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("image_urls").type(JsonFieldType.ARRAY).description("이미지 URL").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 ID")
                        )
                )
        );
    }

    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Test
    void deleteAdminWriteCommunity() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/admin/community/{community_id}", 10))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_delete_community",
                        pathParameters(
                                parameterWithName("community_id").description("어드민 작성 게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 ID")
                        )
                )
        );
    }

    @Test
    void getAdminCategories() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/community/category"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_community_status",
                responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("커뮤니티 카테고리 ID"),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("커뮤니티 카테고리 이름")
                )));
    }

    @Test
    void getCommunities() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/community"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_communities",
                requestParameters(
                        parameterWithName("category_id").description("카테고리 아이디").optional(),
                        parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                        parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10)),
                        parameterWithName("sort").description("정렬 필드").optional().attributes(getDefault("sortedAt")),
                        parameterWithName("order").description("정렬 방향").optional().attributes(getDefault("DESC")),
                        parameterWithName("search").description("검색 - 검색필드,검색어").optional(),
                        parameterWithName("start_at").description("검색 시작일자").optional(),
                        parameterWithName("end_at").description("검색 종료일자").optional()
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 커뮤니티 개수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("커뮤니티 ID"),
                        fieldWithPath("content.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부").optional(),
                        fieldWithPath("content.[].is_top_fix").type(JsonFieldType.BOOLEAN).description("상단 고정 여부").optional(),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                        fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("content.[].event_title").type(JsonFieldType.STRING).description("이벤트 제목").optional(),
                        fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("content.[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("content.[].votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                        fieldWithPath("content.[].votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                        fieldWithPath("content.[].votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                        fieldWithPath("content.[].votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                        fieldWithPath("content.[].votes.[].is_voted").type(JsonFieldType.BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                        fieldWithPath("content.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("content.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].member").type(JsonFieldType.OBJECT).description("작성자 정보.").optional(),
                        fieldWithPath("content.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                        fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("content.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("content.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("content.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("content.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목")
                )
        ));
    }

    @Test
    void getCommunity() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/community/{community_id}", 4))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_community",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 ID"),
                        fieldWithPath("is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부").optional(),
                        fieldWithPath("is_top_fix").type(JsonFieldType.BOOLEAN).description("상단 고정 여부").optional(),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("event_title").type(JsonFieldType.STRING).description("이벤트 제목").optional(),
                        fieldWithPath("contents").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                        fieldWithPath("votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                        fieldWithPath("votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                        fieldWithPath("votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                        fieldWithPath("votes.[].is_voted").type(JsonFieldType.BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                        fieldWithPath("view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보.").optional(),
                        fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                        fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("category.title").type(JsonFieldType.STRING).description("카테고리 제목")
                )
        ));
    }

    @Transactional
    @Test
    void hideCommunity() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/community/{community_id}/hiding", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_hide_community",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("숨김 처리 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 ID")
                )
        ));
    }

    @Transactional
    @Test
    void winCommunity() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/community/{community_id}/winning", 8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_win_community",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("당첨 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 ID")
                )
        ));
    }

    @Transactional
    @Test
    void fixCommunity() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/community/{community_id}/fix", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_fix_community",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 ID")
                )
        ));
    }

}
