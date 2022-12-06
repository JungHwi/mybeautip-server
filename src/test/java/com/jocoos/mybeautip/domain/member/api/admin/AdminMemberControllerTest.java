package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMemberControllerTest extends RestDocsTestSupport {


    @Test
    void getMemberStatuses() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/status"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_status",
                responseFields(
                        fieldWithPath("[].status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                        fieldWithPath("[].status_name").type(JsonFieldType.STRING).description("회원 상태 이름"),
                        fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("회원수")
                )));
    }

    @Test
    void getMemberBasicDetail() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/{member_id}", 4))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_basic_detail",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                        fieldWithPath("avatar_url").type(JsonFieldType.STRING).description("아바타 이미지 URL"),
                        fieldWithPath("username").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일").optional(),
                        fieldWithPath("phone_number").type(JsonFieldType.STRING).description("전화번호").optional(),
                        fieldWithPath("point").type(JsonFieldType.NUMBER).description("보유 포인트"),
                        fieldWithPath("expiry_point").type(JsonFieldType.NUMBER).description("이번달 만료예정 포인트"),
                        fieldWithPath("is_pushable").type(JsonFieldType.BOOLEAN).description("푸시 알림 동의 여부"),
                        fieldWithPath("is_agree_marketing_term").type(JsonFieldType.BOOLEAN).description("마케팅 동의 여부"),
                        fieldWithPath("grant_type").type(JsonFieldType.STRING).description("가입 경로").optional(),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("가입일자"),
                        fieldWithPath("modified_at").type(JsonFieldType.STRING).description("수정일자"),
                        fieldWithPath("normal_community_count").type(JsonFieldType.NUMBER).description("정상 게시물 작성 수"),
                        fieldWithPath("normal_community_comment_count").type(JsonFieldType.NUMBER).description("정상 게시물 댓글 작성 수"),
                        fieldWithPath("normal_video_comment_count").type(JsonFieldType.NUMBER).description("정상 비디오 댓글 작성 수"),
                        fieldWithPath("total_community_count").type(JsonFieldType.NUMBER).description("전체 게시물 작성 수"),
                        fieldWithPath("total_community_comment_count").type(JsonFieldType.NUMBER).description("전체 커뮤니티 댓글 작성 수"),
                        fieldWithPath("total_video_comment_count").type(JsonFieldType.NUMBER).description("전체 비디오 댓글 작성 수"),
                        fieldWithPath("invited_friend_count").type(JsonFieldType.NUMBER).description("초대한 친구들 수"),
                        fieldWithPath("age_group").type(JsonFieldType.NUMBER).description("연령대, 10 단위 ex) 10, 20, 30").optional(),
                        fieldWithPath("skin_type").type(JsonFieldType.STRING).description(generateLinkCode(SKIN_TYPE)).optional(),
                        fieldWithPath("skin_worry").type(JsonFieldType.ARRAY).description(generateLinkCode(SKIN_WORRY)).optional(),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소").optional(),
                        fieldWithPath("memo").type(JsonFieldType.ARRAY).description("괸리자 작성 메모 목록").optional(),
                        fieldWithPath("memo.[].id").type(JsonFieldType.NUMBER).description("괸리자 작성 메모 ID"),
                        fieldWithPath("memo.[].content").type(JsonFieldType.STRING).description("괸리자 작성 메모 내용"),
                        fieldWithPath("memo.[].member").type(JsonFieldType.OBJECT).description("괸리자 작성 메모 작성자"),
                        fieldWithPath("memo.[].member.id").type(JsonFieldType.OBJECT).description("괸리자 작성 메모 작성자 ID"),
                        fieldWithPath("memo.[].member.username").type(JsonFieldType.OBJECT).description("괸리자 작성 메모 작성자 닉네임"),
                        fieldWithPath("memo.[].created_at").type(JsonFieldType.STRING).description("생성일자").attributes(getZonedDateFormat())
                )));
    }

    @Test
    void getMembers() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_members",
                requestParameters(
                        parameterWithName("page").attributes(getDefault(1)).description("페이지 넘버").optional(),
                        parameterWithName("size").attributes(getDefault(10)).description("페이지 내 컨텐츠 개수").optional(),
                        parameterWithName("status").description("멤버 상태").optional(),
                        parameterWithName("grant_type").description("가입").optional(),
                        parameterWithName("search").description("검색 (검색필드,검색어) 형식").optional(),
                        parameterWithName("start_at").description("검색 시작 일자").optional(),
                        parameterWithName("end_at").description("검색 종료 일자").optional()
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 회원 수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("회원 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("회원 ID"),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)),
                        fieldWithPath("content.[].grant_type").type(JsonFieldType.STRING).description("가입 경로").optional(),
                        fieldWithPath("content.[].avatar_url").type(JsonFieldType.STRING).description("아바타 이미지 URL"),
                        fieldWithPath("content.[].username").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("content.[].email").type(JsonFieldType.STRING).description("이메일").optional(),
                        fieldWithPath("content.[].point").type(JsonFieldType.NUMBER).description("보유 포인트"),
                        fieldWithPath("content.[].community_count").type(JsonFieldType.NUMBER).description("게시물 작성 수"),
                        fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("댓글 작성 수"),
                        fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고된 수"),
                        fieldWithPath("content.[].order_count").type(JsonFieldType.NUMBER).description("주문 수"),
                        fieldWithPath("content.[].is_pushable").type(JsonFieldType.BOOLEAN).description("푸시 알림 동의 여부"),
                        fieldWithPath("content.[].is_agree_marketing_term").type(JsonFieldType.BOOLEAN).description("마케팅 동의 여부"),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("가입일자").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].modified_at").type(JsonFieldType.STRING).description("수정일자").attributes(getZonedDateFormat())
                )));
    }

    @Test
    void getMemberPointHistory() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/{member_id}/point", 4))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_point_history",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID")
                ),
                requestParameters(
                        parameterWithName("page").attributes(getDefault(1)).description("페이지 넘버").optional(),
                        parameterWithName("size").attributes(getDefault(10)).description("페이지 내 컨텐츠 개수").optional()
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 포인트 내역 수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("포인트 내역 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("포인트 ID"),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(POINT_STATUS)),
                        fieldWithPath("content.[].reason").type(JsonFieldType.STRING).description("이유"),
                        fieldWithPath("content.[].point").type(JsonFieldType.NUMBER).description("포인트"),
                        fieldWithPath("content.[].earned_at").type(JsonFieldType.STRING).description("생성일자").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].expiry_at").type(JsonFieldType.STRING).description("만료일자").attributes(getZonedDateFormat()).optional()
                )));
    }

    @Test
    void getMemberReportHistory() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/{member_id}/report", 4))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_report_history",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID")
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 신고 내역 수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("신고 내역 목록").optional(),
                        fieldWithPath("content.[].id").type(JsonFieldType.STRING).description("신고 ID, type + id 형태"),
                        fieldWithPath("content.[].accuser").type(JsonFieldType.OBJECT).description("신고자"),
                        fieldWithPath("content.[].accuser.id").type(JsonFieldType.NUMBER).description("신고자 ID"),
                        fieldWithPath("content.[].accuser.username").type(JsonFieldType.STRING).description("신고자 닉네임"),
                        fieldWithPath("content.[].reason").type(JsonFieldType.STRING).description("신고 사유"),
                        fieldWithPath("content.[].reported_at").type(JsonFieldType.STRING).description("신고일자").attributes(getZonedDateFormat())
                )));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void patchMemberStatus() throws Exception {
        MemberStatusRequest request = new MemberStatusRequest(MemberStatus.SUSPENDED);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/member/{member_id}/status", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_patch_member_status",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID")
                ),
                requestFields(
                        fieldWithPath("after_status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)),
                        fieldWithPath("description").ignored(),
                        fieldWithPath("operation_type").ignored(),
                        fieldWithPath("target_id").ignored()
                )));
    }
}
