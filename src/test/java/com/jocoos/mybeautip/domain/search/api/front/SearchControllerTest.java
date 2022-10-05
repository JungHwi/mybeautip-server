package com.jocoos.mybeautip.domain.search.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest extends RestDocsTestSupport {

    @Test
    void searchCommunityTest() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/search")
                        .param("type", "COMMUNITY")
                        .param("keyword", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("search_community",
                requestParameters(
                        parameterWithName("type").description("검색 타입").optional().description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.SEARCH_TYPE)),
                        parameterWithName("keyword").description("검색어"),
                        parameterWithName("cursor").description("커서").optional().attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                        parameterWithName("size").description("조회 개").optional().attributes(getDefault(20))
                ),
                responseFields(
                        fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보").attributes(getZonedDateMilliFormat()),
                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("검색 결과 수"),
                        fieldWithPath("community").type(JsonFieldType.ARRAY).description("커뮤니티 글 목록"),
                        fieldWithPath("community.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("community.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                        fieldWithPath("community.[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
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
                        fieldWithPath("community.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("community.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("community.[].member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("community.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("community.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("community.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("community.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("community.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("community.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("community.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트"))));
    }

    @Test
    void searchVideoTest() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/search")
                        .param("type", "VIDEO")
                        .param("keyword", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("search_video",
                requestParameters(
                        parameterWithName("type").description("검색 타입").optional().description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.SEARCH_TYPE)),
                        parameterWithName("keyword").description("검색어"),
                        parameterWithName("cursor").description("커서").optional().attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                        parameterWithName("size").description("조회 개수").optional().attributes(getDefault(20))
                ),
                responseFields(
                        fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보").attributes(getZonedDateMilliFormat()),
                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("검색 결과 수"),
                        fieldWithPath("video").type(JsonFieldType.ARRAY).description("비디오 글 목록"),
                        fieldWithPath("video.[].id").type(JsonFieldType.NUMBER).description("비디오 ID"),
                        fieldWithPath("video.[].video_key").type(JsonFieldType.STRING).description("비디오 키"),
                        fieldWithPath("video.[].live_key").type(JsonFieldType.STRING).description("라이브 키").optional(),
                        fieldWithPath("video.[].output_type").type(JsonFieldType.STRING).description("").optional(),
                        fieldWithPath("video.[].type").type(JsonFieldType.STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                        fieldWithPath("video.[].state").type(JsonFieldType.STRING).description("방송 상태. VOD 뿐."),
                        fieldWithPath("video.[].locked").type(JsonFieldType.BOOLEAN).description("잠금 여부"),
                        fieldWithPath("video.[].muted").type(JsonFieldType.BOOLEAN).description("음소거 여부"),
                        fieldWithPath("video.[].visibility").type(JsonFieldType.STRING).description("노출 여부"),
                        fieldWithPath("video.[].category").type(JsonFieldType.ARRAY).description("카테고리 정보").optional(),
                        fieldWithPath("video.[].category.[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("video.[].category.[].type").type(JsonFieldType.STRING).description("카테고리 구분"),
                        fieldWithPath("video.[].category.[].title").type(JsonFieldType.STRING).description("카테고리 타이틀"),
                        fieldWithPath("video.[].category.[].shape_url").type(JsonFieldType.STRING).description("카테고리 쉐입 URL").optional(),
                        fieldWithPath("video.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("video.[].content").type(JsonFieldType.STRING).description("컨텐츠").optional(),
                        fieldWithPath("video.[].url").type(JsonFieldType.STRING).description("비디오 파일 주소"),
                        fieldWithPath("video.[].original_filename").type(JsonFieldType.STRING).description("비디오 파일명").optional(),
                        fieldWithPath("video.[].thumbnail_path").type(JsonFieldType.STRING).description("썸네일 경로").optional(),
                        fieldWithPath("video.[].thumbnail_url").type(JsonFieldType.STRING).description("썸네일 URL").optional(),
                        fieldWithPath("video.[].chat_room_id").type(JsonFieldType.STRING).description("채팅방 아이디").optional(),
                        fieldWithPath("video.[].duration").type(JsonFieldType.NUMBER).description("방송 길이. mm 초 단위"),
                        fieldWithPath("video.[].total_watch_count").type(JsonFieldType.NUMBER).description("총 시청").optional(),
                        fieldWithPath("video.[].real_watch_count").type(JsonFieldType.NUMBER).description("실시청자수").optional(),
                        fieldWithPath("video.[].watch_count").type(JsonFieldType.NUMBER).description("실시간 시청자수"),
                        fieldWithPath("video.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("video.[].heart_count").type(JsonFieldType.NUMBER).description("하트수"),
                        fieldWithPath("video.[].like_count").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("video.[].comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                        fieldWithPath("video.[].order_count").type(JsonFieldType.NUMBER).description("주문수"),
                        fieldWithPath("video.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("video.[].data").type(JsonFieldType.STRING).description("상품 정보등").optional(),
                        fieldWithPath("video.[].related_goods_count").type(JsonFieldType.NUMBER).description("관련 상품 갯수").optional(),
                        fieldWithPath("video.[].related_goods_thumbnail_url").type(JsonFieldType.STRING).description("상품 대표 URL").optional(),
                        fieldWithPath("video.[].like_id").type(JsonFieldType.NUMBER).description("좋아요 아이디").optional(),
                        fieldWithPath("video.[].scrap_id").type(JsonFieldType.NUMBER).description("스크랩 아이디").optional(),
                        fieldWithPath("video.[].blocked").type(JsonFieldType.BOOLEAN).description("차단 여부").optional(),
                        fieldWithPath("video.[].owner").type(JsonFieldType.OBJECT).description("비디오 작성자 정보"),
                        fieldWithPath("video.[].owner.id").type(JsonFieldType.NUMBER).description("아이디"),
                        fieldWithPath("video.[].owner.tag").type(JsonFieldType.STRING).description("태그"),
                        fieldWithPath("video.[].owner.status").type(JsonFieldType.STRING).description("상태"),
                        fieldWithPath("video.[].owner.username").type(JsonFieldType.STRING).description("유저명"),
                        fieldWithPath("video.[].owner.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("video.[].owner.phone_number").type(JsonFieldType.STRING).description("전화번호"),
                        fieldWithPath("video.[].owner.avatar_url").type(JsonFieldType.STRING).description("아바타 URL"),
                        fieldWithPath("video.[].owner.follower_count").type(JsonFieldType.NUMBER).description("팔로워 수"),
                        fieldWithPath("video.[].owner.following_count").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                        fieldWithPath("video.[].owner.video_count").type(JsonFieldType.NUMBER).description("비디오 수"),
                        fieldWithPath("video.[].owner.created_at").type(JsonFieldType.NUMBER).description("회원가입일"),
                        fieldWithPath("video.[].owner.modified_at").type(JsonFieldType.NUMBER).description("정보수정일"),
                        fieldWithPath("video.[].owner.deleted_at").type(JsonFieldType.NUMBER).description("탈퇴일").optional(),
                        fieldWithPath("video.[].owner.permission").type(JsonFieldType.OBJECT).description("권한").optional(),
                        fieldWithPath("video.[].owner.permission.chat_post").type(JsonFieldType.BOOLEAN).description("post 권한").optional(),
                        fieldWithPath("video.[].owner.permission.comment_post").type(JsonFieldType.BOOLEAN).description("댓글 권한").optional(),
                        fieldWithPath("video.[].owner.permission.live_post").type(JsonFieldType.BOOLEAN).description("라이브 권한").optional(),
                        fieldWithPath("video.[].owner.permission.motd_post").type(JsonFieldType.BOOLEAN).description("motd 권한").optional(),
                        fieldWithPath("video.[].owner.permission.revenue_return").type(JsonFieldType.BOOLEAN).description("수익배분 권한").optional(),
                        fieldWithPath("video.[].created_at").type(JsonFieldType.NUMBER).description("생성일시"))));

    }

    @Test
    void countTest() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/search/count")
                        .param("type", "VIDEO")
                        .param("keyword", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("count",
                requestParameters(
                        parameterWithName("type").description("검색 타입").optional().description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.SEARCH_TYPE)),
                        parameterWithName("keyword").description("검색어")
                ),
                responseFields(
                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("검색 개수")
                )));
    }
}
