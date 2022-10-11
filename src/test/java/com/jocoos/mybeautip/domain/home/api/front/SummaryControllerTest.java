package com.jocoos.mybeautip.domain.home.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SummaryControllerTest extends RestDocsTestSupport {


    @Test
    void summaryEvent() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/summary/event"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("summary_event",
                responseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("[].type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.EVENT_TYPE)),
                        fieldWithPath("[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.EVENT_STATUS)),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("[].color").type(JsonFieldType.STRING).description("이벤트 색깔 정보"),
                        fieldWithPath("[].thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                        fieldWithPath("[].banner_image_url").type(JsonFieldType.STRING).description("배너 이미지 URL").optional(),
                        fieldWithPath("[].start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                        fieldWithPath("[].end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat())
                )));
    }

    @Test
    @Disabled
    void summaryCommunity() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/summary/community"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("summary_community",
                responseFields(
                        fieldWithPath("top").type(JsonFieldType.OBJECT).description("커뮤니티 메인 상단 탭 정보"),
                        fieldWithPath("top.category").type(JsonFieldType.ARRAY).description("커뮤니티 메인 상단 탭 카테고리"),
                        fieldWithPath("top.category.[].id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                        fieldWithPath("top.category.[].type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("top.category.[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("top.category.[].hint").type(JsonFieldType.STRING).description("힌트"),
                        fieldWithPath("top.content").type(JsonFieldType.ARRAY).description("커뮤니티 메인 상단 게시글 정보"),
                        fieldWithPath("top.content.[].category_id").type(JsonFieldType.NUMBER).description("커뮤니티 카테고리 아이디"),
                        fieldWithPath("top.content.[].community").type(JsonFieldType.ARRAY).description("게시글 목록"),
                        fieldWithPath("top.content.[].community.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("top.content.[].community.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                        fieldWithPath("top.content.[].community.[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                        fieldWithPath("top.content.[].community.[].event_id").type(JsonFieldType.NUMBER).description("이벤트 ID").optional(),
                        fieldWithPath("top.content.[].community.[].event_title").type(JsonFieldType.STRING).description("이벤트 제목").optional(),
                        fieldWithPath("top.content.[].community.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("top.content.[].community.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("top.content.[].community.[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("top.content.[].community.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("top.content.[].community.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("top.content.[].community.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("top.content.[].community.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("top.content.[].community.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("top.content.[].community.[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                        fieldWithPath("top.content.[].community.[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                        fieldWithPath("top.content.[].community.[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                        fieldWithPath("top.content.[].community.[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                        fieldWithPath("top.content.[].community.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("top.content.[].community.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("top.content.[].community.[].member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("top.content.[].community.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("top.content.[].community.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("top.content.[].community.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("top.content.[].community.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("top.content.[].community.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("top.content.[].community.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("top.content.[].community.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트"),

                        fieldWithPath("vote").type(JsonFieldType.ARRAY).description("커뮤니티 메인 결정픽 탭 정보"),
                        fieldWithPath("vote.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("vote.[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                        fieldWithPath("vote.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("vote.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("vote.[].['file_url']").type(JsonFieldType.ARRAY).description("파일 URL List").optional(),
                        fieldWithPath("vote.[].votes").type(JsonFieldType.ARRAY).description("투표 파일 List").optional(),
                        fieldWithPath("vote.[].votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                        fieldWithPath("vote.[].votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                        fieldWithPath("vote.[].votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                        fieldWithPath("vote.[].votes.[].is_voted").type(JsonFieldType.BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                        fieldWithPath("vote.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("vote.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("vote.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("vote.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("vote.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("vote.[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                        fieldWithPath("vote.[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                        fieldWithPath("vote.[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                        fieldWithPath("vote.[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                        fieldWithPath("vote.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("vote.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("vote.[].member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("vote.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("vote.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("vote.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("vote.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("vote.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("vote.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("vote.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트"),

                        fieldWithPath("blind").type(JsonFieldType.ARRAY).description("커뮤니티 메인 속닥속닥탭 정보"),
                        fieldWithPath("blind.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("blind.[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                        fieldWithPath("blind.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("blind.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("blind.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("blind.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("blind.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("blind.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("blind.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                        fieldWithPath("blind.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("blind.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                        fieldWithPath("blind.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                        fieldWithPath("blind.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                )));

    }

    @Test
    void summaryVideo() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/summary/video"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("summary_video",
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("비디오 ID"),
                                fieldWithPath("[].video_key").type(JsonFieldType.STRING).description("비디오 키"),
                                fieldWithPath("[].live_key").type(JsonFieldType.STRING).description("라이브 키").optional(),
                                fieldWithPath("[].output_type").type(JsonFieldType.STRING).description("").optional(),
                                fieldWithPath("[].type").type(JsonFieldType.STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                                fieldWithPath("[].state").type(JsonFieldType.STRING).description("방송 상태. VOD 뿐."),
                                fieldWithPath("[].locked").type(JsonFieldType.BOOLEAN).description("잠금 여부"),
                                fieldWithPath("[].muted").type(JsonFieldType.BOOLEAN).description("음소거 여부"),
                                fieldWithPath("[].visibility").type(JsonFieldType.STRING).description("노출 여부"),
                                fieldWithPath("[].category").type(JsonFieldType.ARRAY).description("카테고리 정보").optional(),
                                fieldWithPath("[].category.[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("[].category.[].type").type(JsonFieldType.STRING).description("카테고리 구분"),
                                fieldWithPath("[].category.[].title").type(JsonFieldType.STRING).description("카테고리 타이틀"),
                                fieldWithPath("[].category.[].shape_url").type(JsonFieldType.STRING).description("카테고리 쉐입 URL").optional(),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("컨텐츠").optional(),
                                fieldWithPath("[].url").type(JsonFieldType.STRING).description("비디오 파일 주소"),
                                fieldWithPath("[].original_filename").type(JsonFieldType.STRING).description("비디오 파일명").optional(),
                                fieldWithPath("[].thumbnail_path").type(JsonFieldType.STRING).description("썸네일 경로").optional(),
                                fieldWithPath("[].thumbnail_url").type(JsonFieldType.STRING).description("썸네일 URL").optional(),
                                fieldWithPath("[].chat_room_id").type(JsonFieldType.STRING).description("채팅방 아이디").optional(),
                                fieldWithPath("[].duration").type(JsonFieldType.NUMBER).description("방송 길이. mm 초 단위"),
                                fieldWithPath("[].total_watch_count").type(JsonFieldType.NUMBER).description("총 시청").optional(),
                                fieldWithPath("[].real_watch_count").type(JsonFieldType.NUMBER).description("실시청자수").optional(),
                                fieldWithPath("[].watch_count").type(JsonFieldType.NUMBER).description("실시간 시청자수"),
                                fieldWithPath("[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("[].heart_count").type(JsonFieldType.NUMBER).description("하트수"),
                                fieldWithPath("[].like_count").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("[].comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                                fieldWithPath("[].order_count").type(JsonFieldType.NUMBER).description("주문수"),
                                fieldWithPath("[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("[].data").type(JsonFieldType.STRING).description("상품 정보등").optional(),
                                fieldWithPath("[].related_goods_count").type(JsonFieldType.NUMBER).description("관련 상품 갯수").optional(),
                                fieldWithPath("[].related_goods_thumbnail_url").type(JsonFieldType.STRING).description("상품 대표 URL").optional(),
                                fieldWithPath("[].like_id").type(JsonFieldType.NUMBER).description("좋아요 아이디").optional(),
                                fieldWithPath("[].scrap_id").type(JsonFieldType.NUMBER).description("스크랩 아이디").optional(),
                                fieldWithPath("[].blocked").type(JsonFieldType.BOOLEAN).description("차단 여부").optional(),
                                fieldWithPath("[].owner").type(JsonFieldType.OBJECT).description("비디오 작성자 정보"),
                                fieldWithPath("[].owner.id").type(JsonFieldType.NUMBER).description("아이디"),
                                fieldWithPath("[].owner.tag").type(JsonFieldType.STRING).description("태그"),
                                fieldWithPath("[].owner.status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("[].owner.username").type(JsonFieldType.STRING).description("유저명"),
                                fieldWithPath("[].owner.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("[].owner.phone_number").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("[].owner.avatar_url").type(JsonFieldType.STRING).description("아바타 URL"),
                                fieldWithPath("[].owner.follower_count").type(JsonFieldType.NUMBER).description("팔로워 수"),
                                fieldWithPath("[].owner.following_count").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                                fieldWithPath("[].owner.video_count").type(JsonFieldType.NUMBER).description("비디오 수"),
                                fieldWithPath("[].owner.created_at").type(JsonFieldType.NUMBER).description("회원가입일"),
                                fieldWithPath("[].owner.modified_at").type(JsonFieldType.NUMBER).description("정보수정일"),
                                fieldWithPath("[].owner.permission").type(JsonFieldType.OBJECT).description("권한").optional(),
                                fieldWithPath("[].owner.permission.chat_post").type(JsonFieldType.BOOLEAN).description("post 권한").optional(),
                                fieldWithPath("[].owner.permission.comment_post").type(JsonFieldType.BOOLEAN).description("댓글 권한").optional(),
                                fieldWithPath("[].owner.permission.live_post").type(JsonFieldType.BOOLEAN).description("라이브 권한").optional(),
                                fieldWithPath("[].owner.permission.motd_post").type(JsonFieldType.BOOLEAN).description("motd 권한").optional(),
                                fieldWithPath("[].owner.permission.revenue_return").type(JsonFieldType.BOOLEAN).description("수익배분 권한").optional(),
                                fieldWithPath("[].created_at").type(JsonFieldType.NUMBER).description("생성일시")
                        )
                )
        );
    }
}
