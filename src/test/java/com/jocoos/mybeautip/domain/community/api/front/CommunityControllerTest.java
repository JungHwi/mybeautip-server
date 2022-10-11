package com.jocoos.mybeautip.domain.community.api.front;


import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommunityControllerTest extends RestDocsTestSupport {

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

        result.andDo(document("write_community",
                        requestFields(
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("event_id").type(JsonFieldType.NUMBER).description("이벤트 아이디. 드립N드림 일때 관련된 이벤트 아이디.").optional(),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목. 수근수근에서만 필수").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("['files']").type(JsonFieldType.ARRAY).description("파일 작업 정보 목록").optional(),
                                fieldWithPath("['files'].operation").type(JsonFieldType.STRING).description("파일 상태").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.FILE_OPERATION_TYPE)),
                                fieldWithPath("['files'].url").type(JsonFieldType.STRING).description("파일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
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
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void uploadFiles() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .multipart("/api/1/community/files")
                        .file("files", "mockup".getBytes()))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("upload_file_community",
                        requestParts(
                                partWithName("files").description("업로드할 파일 목록")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("UPLOAD 된 파일 URL")
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

        result.andDo(document("get_communities",
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
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
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

    @Test
    void getCommunity() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
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
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
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

        result.andDo(document("edit_community",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("['files']").type(JsonFieldType.ARRAY).description("파일 작업 정보 목록").optional(),
                                fieldWithPath("['files'].operation").type(JsonFieldType.STRING).description("파일 상태").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.FILE_OPERATION_TYPE)),
                                fieldWithPath("['files'].url").type(JsonFieldType.STRING).description("파일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
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
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void deleteCommunity() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/1/community/{community_id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("delete_community",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void likeCommunity() throws Exception {
        BooleanDto bool = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/like", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bool)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("like_community",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        requestFields(
                                fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("좋아요 여부")
                        ),
                        responseFields(
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("커뮤니티 좋아요 수")
                        ))
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void reportCommunity() throws Exception {
        ReportRequest report = ReportRequest.builder()
                .isReport(true)
                .description("신고사유")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/report", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("report_community",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("신고 사유. 신고여부가 true 일때만 필수").optional()
                ),
                responseFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("커뮤니티 신고수")
                ))
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void isReportCommunity() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}/report", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("check_report_community",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                responseFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("커뮤니티 신고수")
                ))
        );
    }

    @Test
    @Transactional
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void scrap() throws Exception {
        BooleanDto bool = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/scrap", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bool)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("community_scrap",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("스크랩 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("스크랩 아이디"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description("스크랩 타입"),
                        fieldWithPath("community_id").type(JsonFieldType.NUMBER).description("스크랩 커뮤니티 아이디"),
                        fieldWithPath("is_scrap").type(JsonFieldType.BOOLEAN).description("스크랩 여부"),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("스크랩 생성일시")
                ))
        );
    }

}


