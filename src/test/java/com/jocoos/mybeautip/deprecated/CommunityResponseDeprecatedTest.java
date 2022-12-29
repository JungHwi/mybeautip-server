package com.jocoos.mybeautip.deprecated;


import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;
import static com.jocoos.mybeautip.domain.search.code.SearchType.COMMUNITY;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.SEARCH_TYPE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityResponseDeprecatedTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void writeCommunity() throws Exception {
        WriteCommunityRequest request = WriteCommunityRequest.builder()
                .categoryId(4L)
                .title("Mock Title")
                .contents("Mock Contents")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/1/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("write_community_v1",
                        requestFields(
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("event_id").type(JsonFieldType.NUMBER).description("이벤트 아이디. 드립N드림 일때 관련된 이벤트 아이디.").optional(),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목. 수근수근에서만 필수").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("['files']").type(JsonFieldType.ARRAY).description("파일 작업 정보 목록").optional(),
                                fieldWithPath("['files'].operation").type(JsonFieldType.STRING).description("파일 상태").description(generateLinkCode(DocumentLinkGenerator.DocUrl.FILE_OPERATION_TYPE)),
                                fieldWithPath("['files'].url").type(JsonFieldType.STRING).description("파일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                                fieldWithPath("votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                                fieldWithPath("votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                                fieldWithPath("votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                                fieldWithPath("votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                                fieldWithPath("votes.[].is_voted").type(JsonFieldType.BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                                fieldWithPath("view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                                fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    void getCommunities() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_communities_v1",
                        requestParameters(
                                parameterWithName("category_id").description("카테고리 아이디").optional(),
                                parameterWithName("event_id").description("이벤트 아이디. Drip Category 일때만 필수.").optional(),
                                parameterWithName("cursor").description("커서").optional().attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                                parameterWithName("size").description("").optional().attributes(getDefault(20))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보").attributes(getZonedDateMilliFormat()),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 글 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("content.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("content.[].event_id").type(JsonFieldType.NUMBER).description("이벤트 ID").optional(),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목").optional(),
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
                                fieldWithPath("content.[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("content.[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("content.[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("content.[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("content.[].relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                                fieldWithPath("content.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                                fieldWithPath("content.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                                fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("content.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("content.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("content.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("content.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    void getCommunity() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community_v1",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("event_id").type(JsonFieldType.NUMBER).description("이벤트 ID").optional(),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
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
                                fieldWithPath("relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void editCommunity() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Test Title");
        map.put("contents", "Test Contents");
        map.put("files", null);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .put("/api/1/community/{community_id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("edit_community_v1",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("['files']").type(JsonFieldType.ARRAY).description("파일 작업 정보 목록").optional(),
                                fieldWithPath("['files'].operation").type(JsonFieldType.STRING).description("파일 상태").description(generateLinkCode(DocumentLinkGenerator.DocUrl.FILE_OPERATION_TYPE)),
                                fieldWithPath("['files'].url").type(JsonFieldType.STRING).description("파일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
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
                                fieldWithPath("relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }


    @Test
    void summaryCommunityTop() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/summary/community/top"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("summary_community_top_v1",
                responseFields(
                        fieldWithPath("category").type(JsonFieldType.ARRAY).description("커뮤니티 메인 상단 탭 카테고리"),
                        fieldWithPath("category.[].id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                        fieldWithPath("category.[].type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("category.[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("category.[].hint").type(JsonFieldType.STRING).description("힌트"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 메인 상단 게시글 정보"),
                        fieldWithPath("content.[].category_id").type(JsonFieldType.NUMBER).description("커뮤니티 카테고리 아이디"),
                        fieldWithPath("content.[].community").type(JsonFieldType.ARRAY).description("게시글 목록"),
                        fieldWithPath("content.[].community.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("content.[].community.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                        fieldWithPath("content.[].community.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                        fieldWithPath("content.[].community.[].event_id").type(JsonFieldType.NUMBER).description("이벤트 ID").optional(),
                        fieldWithPath("content.[].community.[].event_title").type(JsonFieldType.STRING).description("이벤트 제목").optional(),
                        fieldWithPath("content.[].community.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("content.[].community.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("content.[].community.[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("content.[].community.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("content.[].community.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("content.[].community.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("content.[].community.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("content.[].community.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].community.[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                        fieldWithPath("content.[].community.[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                        fieldWithPath("content.[].community.[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                        fieldWithPath("content.[].community.[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                        fieldWithPath("content.[].community.[].relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                        fieldWithPath("content.[].community.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("content.[].community.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("content.[].community.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("content.[].community.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("content.[].community.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("content.[].community.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("content.[].community.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("content.[].community.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("content.[].community.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("content.[].community.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                )));

    }

    @Test
    void summaryCommunityVote() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/summary/community/{type}", VOTE))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("summary_community_vote_v1",
                pathParameters(
                        parameterWithName("type").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE))
                ),
                responseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("Vote 커뮤니티 목록").optional(),
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                        fieldWithPath("[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("[].votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                        fieldWithPath("[].votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                        fieldWithPath("[].votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                        fieldWithPath("[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("[].comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                        fieldWithPath("[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                        fieldWithPath("[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                        fieldWithPath("[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                        fieldWithPath("[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                        fieldWithPath("[].relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                        fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("작성자 정보"),
                        fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                )));
    }

    @Test
    void summaryCommunityBlind() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/summary/community/{type}", BLIND))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("summary_community_blind_v1",
                pathParameters(
                        parameterWithName("type").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE))
                ),
                responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                        fieldWithPath("[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("[].votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                        fieldWithPath("[].votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                        fieldWithPath("[].votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                        fieldWithPath("[].votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                        fieldWithPath("[].votes.[].is_voted").type(JsonFieldType.BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                        fieldWithPath("[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("[].comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                        fieldWithPath("[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                        fieldWithPath("[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                        fieldWithPath("[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                        fieldWithPath("[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                        fieldWithPath("[].relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("글 스크랩 여부"),
                        fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("작성자 정보"),
                        fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                )));
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getMyCommunities() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/my/community")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_my_communities_v1",
                        requestParameters(
                                parameterWithName("cursor").description("커서").optional().attributes(getDefault(MAX_LONG_STRING)),
                                parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(20))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보"),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 글 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("content.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                                fieldWithPath("content.[].file_url").type(JsonFieldType.STRING).description("메인 파일 URL").optional(),
                                fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                                fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("content.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("content.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("content.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getScrap() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/my/scrap")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community_scraps_v1",
                        requestParameters(
                                parameterWithName("cursor").description("커서").optional(),
                                parameterWithName("size").description("조회 갯수").optional().attributes(getDefault(20))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보").attributes(getZonedDateMilliFormat()).optional(),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 스크랩 목록").optional(),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("커뮤니티 ID"),
                                fieldWithPath("content.[].type").type(JsonFieldType.STRING).description("스크랩 타"),
                                fieldWithPath("content.[].scrap_id").type(JsonFieldType.NUMBER).description("스크랩 ID"),
                                fieldWithPath("content.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                                fieldWithPath("content.[].file_url").type(JsonFieldType.ARRAY).description("파일 URL").optional(),
                                fieldWithPath("content.[].votes").type(JsonFieldType.ARRAY).description("투표 URL").optional(),
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
                                fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("content.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("content.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("content.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("content.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    void searchCommunityTest() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/search")
                        .param("type", "COMMUNITY")
                        .param("keyword", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("search_community_v1",
                requestParameters(
                        parameterWithName("type").description("검색 타입").attributes(getDefault(COMMUNITY)).optional().description(generateLinkCode(SEARCH_TYPE)),
                        parameterWithName("keyword").description("검색어 (1자 이상 20자 이하)"),
                        parameterWithName("cursor").description("커서").optional().attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                        parameterWithName("size").description("조회 개").optional().attributes(getDefault(20))
                ),
                responseFields(
                        fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보").attributes(getZonedDateMilliFormat()),
                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("검색 결과 수"),
                        fieldWithPath("community").type(JsonFieldType.ARRAY).description("커뮤니티 글 목록"),
                        fieldWithPath("community.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("community.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                        fieldWithPath("community.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                        fieldWithPath("community.[].event_id").type(JsonFieldType.NUMBER).description("이벤트 ID").optional(),
                        fieldWithPath("community.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("community.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("community.[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("community.[].votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                        fieldWithPath("community.[].votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                        fieldWithPath("community.[].votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                        fieldWithPath("community.[].votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                        fieldWithPath("community.[].votes.[].is_voted").type(JsonFieldType.BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                        fieldWithPath("community.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("community.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("community.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("community.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("community.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("community.[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                        fieldWithPath("community.[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                        fieldWithPath("community.[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                        fieldWithPath("community.[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                        fieldWithPath("community.[].relation_info.is_scrap").type(JsonFieldType.BOOLEAN).description("스크랩 여부"),
                        fieldWithPath("community.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("community.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("community.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("community.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("community.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("community.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("community.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("community.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("community.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("community.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트"))));
    }
}


