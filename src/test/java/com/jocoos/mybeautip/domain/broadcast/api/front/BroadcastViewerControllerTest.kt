package com.jocoos.mybeautip.domain.broadcast.api.front

import com.jocoos.mybeautip.domain.broadcast.BroadcastTestSupport
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastViewerRepository
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.config.restdoc.util.ExceptionFieldsSnippet
import com.jocoos.mybeautip.global.config.restdoc.util.ExceptionFieldsSnippet.exceptionConvertFieldDescriptor
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.global.exception.ErrorCode
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.makeBroadcast
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeViewer
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class BroadcastViewerControllerTest(
    private val broadcastRepository: BroadcastRepository,
    private val broadcastViewerRepository: BroadcastViewerRepository,
    private val memberRepository: MemberRepository
) : BroadcastTestSupport() {

    @Test
    fun search() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        saveViewer(broadcast = broadcast);

        val result: ResultActions = mockMvc.perform(
            get("/api/1/broadcast/{broadcast_id}/viewer", broadcast.id)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_viewers",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestParameters(
                    parameterWithName("type").description(generateLinkCode(BROADCAST_VIEWER_TYPE)).optional(),
                    parameterWithName("cursor").description("커서. 회원 아이디").optional(),
                    parameterWithName("size").description("페이지 사이즈").optional()
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보"),
                    fieldWithPath("content").type(JsonFieldType.ARRAY).description("시청자 목록"),
                    fieldWithPath("content.[].type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("content.[].member_id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                    fieldWithPath("content.[].username").type(JsonFieldType.STRING).description("회원명"),
                    fieldWithPath("content.[].avatar_url").type(JsonFieldType.STRING).description("회원 아바타 URL"),
                    fieldWithPath("content.[].is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("content.[].joined_at").type(JsonFieldType.STRING).description("채팅방 입장 시간").attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun get() {

        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        val viewer = saveViewer(broadcast = broadcast);

        val result: ResultActions = mockMvc.perform(
            get("/api/1/broadcast/{broadcast_id}/viewer/{member_id}", broadcast.id, viewer.memberId)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_viewer",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID"),
                    parameterWithName("member_id").description("회원 ID"),
                ),
                responseFields(
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 아이디").optional(),
                    fieldWithPath("username").type(JsonFieldType.STRING).description("회원명"),
                    fieldWithPath("avatar_url").type(JsonFieldType.STRING).description("회원 아바타 URL"),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("suspended_at").type(JsonFieldType.STRING).description("정지 시간").attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("채팅방 입장 시간").attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun grantManager() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        val viewer = saveViewer(broadcast = broadcast)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            patch("/api/1/broadcast/{broadcast_id}/viewer/{member_id}/manager", broadcast.id, viewer.memberId)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "grant_manager",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID"),
                    parameterWithName("member_id").description("회원 ID"),
                ),
                requestFields(
                    fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("매니저 권한 여부")
                ),
                responseFields(
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("참여 일시").attributes(getZonedDateFormat())
                ),
            )
        )
    }

    @Test
    fun grantManager_access_denied() {
        val errorCode = ErrorCode.ACCESS_DENIED

        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        val viewer = saveViewer(broadcast = broadcast)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            patch("/api/1/broadcast/{broadcast_id}/viewer/{member_id}/manager", 0, viewer.memberId)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().is4xxClientError)
            .andExpect(isErrorCode(errorCode))
            .andDo(print())

        result.andDo(
            document(
                "grant_manager",
                ExceptionFieldsSnippet(
                    errorCode.key,
                    exceptionConvertFieldDescriptor(result, "요청한 유저가 해당 방송의 오너가 아닐때")
                )
            )
        )
    }

    @Test
    fun grantManager_member_not_found() {
        val errorCode = ErrorCode.MEMBER_NOT_FOUND

        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            patch("/api/1/broadcast/{broadcast_id}/viewer/{member_id}/manager", broadcast.id, 0)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().is4xxClientError)
            .andExpect(isErrorCode(errorCode))
            .andDo(print())

        result.andDo(
            document(
                "grant_manager",
                    ExceptionFieldsSnippet(
                        "member_not_found",
                        exceptionConvertFieldDescriptor(result, "대상 유저를 찾을 수 없을때")
                    )
            )
        )
    }

    @Test
    fun suspend() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        val viewer = saveViewer(broadcast = broadcast)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            patch("/api/1/broadcast/{broadcast_id}/viewer/{member_id}/suspend", broadcast.id, viewer.memberId)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "suspend",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID"),
                    parameterWithName("member_id").description("회원 ID"),
                ),
                requestFields(
                    fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("채팅 정지 여부.")
                ),
                responseFields(
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("참여 일시").attributes(getZonedDateFormat()),
                    fieldWithPath("suspended_at").type(JsonFieldType.STRING).description("채팅 정지 일시").attributes(getZonedDateFormat())
                ),
            )
        )
    }

    @Test
    fun exile() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        val viewer = saveViewer(broadcast = broadcast)

        val result: ResultActions = mockMvc.perform(
            patch("/api/1/broadcast/{broadcast_id}/viewer/{member_id}/exile", broadcast.id, viewer.memberId)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "exile",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID"),
                    parameterWithName("member_id").description("회원 ID"),
                ),
                responseFields(
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("참여 일시").attributes(getZonedDateFormat())
                ),
            )
        )
    }

    @Test
    fun visibleMessage() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            patch("/api/1/broadcast/{broadcast_id}/message/{message_id}/visible", broadcast.id, 123)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "visible_message",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID"),
                    parameterWithName("message_id").description("메세지 ID"),
                )
            )
        )
    }

    @Test
    fun join() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)

        val result: ResultActions = mockMvc.perform(
            post("/api/1/broadcast/{broadcast_id}/viewer", broadcast.id)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "broadcast_join",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                responseFields(
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("username").type(JsonFieldType.STRING).description("회원명"),
                    fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("참여 일시").attributes(getZonedDateFormat()),
                    fieldWithPath("broadcast_key").type(JsonFieldType.OBJECT).description("참여자 방송 관련 키 및 토큰 정보"),
                    fieldWithPath("broadcast_key.stream_key").type(JsonFieldType.STRING).description("참여자 Stream Key, 방송 진행자일 경우에만 존재").optional(),
                    fieldWithPath("broadcast_key.gossip_token").type(JsonFieldType.STRING).description("참여자 채팅 토큰"),
                    fieldWithPath("broadcast_key.app_id").type(JsonFieldType.STRING).description("참여자 채팅 앱 ID"),
                    fieldWithPath("broadcast_key.channel_key").type(JsonFieldType.STRING).description("참여자 채팅 채널 키")
                )
            )
        )
    }

    @Test
    fun out() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        saveViewer(broadcast, requestUser)

        val result: ResultActions = mockMvc.perform(
            delete("/api/1/broadcast/{broadcast_id}/viewer", broadcast.id)
                .header(HttpHeaders.AUTHORIZATION, requestUserToken)
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "broadcast_out",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                responseFields(
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("참여 일시").attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun getStreamKeyInfo() {
        val result: ResultActions = mockMvc.perform(
            get("/api/1/broadcast/viewer/stream-key")
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_stream_key",
                responseFields(
                    fieldWithPath("stream_key").type(JsonFieldType.STRING).description("Stream Key."),
                    fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(FFL_STREAM_KEY_STATE)),
                    fieldWithPath("broadcast_id").type(JsonFieldType.NUMBER).description("현재 방송 중이면 방송 ID.").optional(),
                )
            )
        )
    }

    fun saveViewer(
        broadcast: Broadcast,
        member: Member = saveMember()
    ): BroadcastViewer {
        return broadcastViewerRepository.save(makeViewer(broadcast = broadcast, member = member));
    }

    fun saveBroadcast(
        memberId: Long
    ): Broadcast {
        return broadcastRepository.save(makeBroadcast(memberId = memberId, category = defaultBroadcastCategory))
    }

    fun saveMember(): Member {
        return memberRepository.save(makeMember())
    }
}
