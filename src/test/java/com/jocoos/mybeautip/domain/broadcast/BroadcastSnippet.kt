package com.jocoos.mybeautip.domain.broadcast

import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.BROADCAST_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

fun broadcastListResponse(prefix : String): List<FieldDescriptor> = mutableListOf(
    fieldWithPath(prefix + "id").type(NUMBER).description("방송 아이디"),
    fieldWithPath(prefix + "status").type(STRING).description(generateLinkCode(BROADCAST_STATUS)),
    fieldWithPath(prefix + "url").type(STRING).description("방송 URL").optional(),
    fieldWithPath(prefix + "title").type(STRING).description("타이틀"),
    fieldWithPath(prefix + "thumbnail_url").type(STRING).description("썸네일 URL"),
    fieldWithPath(prefix + "viewer_count").type(NUMBER).description("시청자수"),
    fieldWithPath(prefix + "heart_count").type(NUMBER).description("좋아요수"),
    fieldWithPath(prefix + "started_at").type(STRING).description("시작 시간").attributes(getZonedDateFormat()),
    fieldWithPath(prefix + "ended_at").type(STRING).description("종료 시간").attributes(getZonedDateFormat()).optional(),
    fieldWithPath(prefix + "category").type(OBJECT).description("카테고리 정보"),
    fieldWithPath(prefix + "category.id").type(NUMBER).description("카테고리 ID"),
    fieldWithPath(prefix + "category.title").type(STRING).description("카테고리 타이틀"),
    fieldWithPath(prefix + "created_by").type(OBJECT).description("진행자 정보"),
    fieldWithPath(prefix + "created_by.id").type(NUMBER).description("진행자 아이디"),
    fieldWithPath(prefix + "created_by.email").type(STRING).description("진행자 이메일").optional(),
    fieldWithPath(prefix + "created_by.username").type(STRING).description("진행자 닉네임"),
    fieldWithPath(prefix + "created_by.avatar_url").type(STRING).description("진행자 아바타 URL"),
    fieldWithPath(prefix + "relation_info").type(OBJECT).description("요청자 연관 정보"),
    fieldWithPath(prefix + "relation_info.is_notify_needed").type(BOOLEAN).description("요청자 연관 정보 - 알림 필요 여부")
)

fun broadcastResponse(): MutableList<FieldDescriptor> = mutableListOf(
        fieldWithPath("id").type(NUMBER).description("방송 아이디"),
        fieldWithPath("status").type(STRING).description(generateLinkCode(BROADCAST_STATUS)),
        fieldWithPath("url").type(STRING).description("방송 URL").optional(),
        fieldWithPath("title").type(STRING).description("타이틀"),
        fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL"),
        fieldWithPath("notice").type(STRING).description("공지사항").optional(),
        fieldWithPath("can_chat").type(BOOLEAN).description("채팅 가능 여부"),
        fieldWithPath("is_sound_on").type(BOOLEAN).description("사운드 여부"),
        fieldWithPath("is_screen_show").type(BOOLEAN).description("화면 표시 여부"),
        fieldWithPath("viewer_count").type(NUMBER).description("시청자수"),
        fieldWithPath("heart_count").type(NUMBER).description("하트수"),
        fieldWithPath("started_at").type(STRING).description("시작 시간").attributes(getZonedDateFormat()),
        fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
        fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
        fieldWithPath("category.title").type(STRING).description("카테고리 타이틀")
    )

fun broadcastResponseWithCreatedBy(): MutableList<FieldDescriptor> {
    val response = broadcastResponse()
    response.addAll(mutableListOf(
        fieldWithPath("created_by").type(OBJECT).description("진행자 정보"),
        fieldWithPath("created_by.id").type(NUMBER).description("진행자 회원 아이디"),
        fieldWithPath("created_by.email").type(STRING).description("진행자 회원 이메일").optional(),
        fieldWithPath("created_by.username").type(STRING).description("진행자 닉네임"),
        fieldWithPath("created_by.avatar_url").type(STRING).description("진행자 아바타 URL")
    ))
    return response
}

fun broadcastResponseWithCreatedByWithPinMessage(): MutableList<FieldDescriptor> {
    val response = broadcastResponseWithCreatedBy()
    response.addAll(mutableListOf(
        fieldWithPath("pin_message").type(OBJECT).description("고정 메세지 정보").optional(),
        fieldWithPath("pin_message.message_id").type(NUMBER).description("고정 메세지 ID"),
        fieldWithPath("pin_message.message").type(STRING).description("고정 메세지 내용"),
        fieldWithPath("pin_message.created_by").type(OBJECT).description("고정 메세지 작성자 정보"),
        fieldWithPath("pin_message.created_by.id").type(NUMBER).description("고정 메세지 작성자 ID"),
        fieldWithPath("pin_message.created_by.username").type(STRING).description("고정 메세지 작성자 닉네임"),
        fieldWithPath("pin_message.created_by.avatar_url").type(STRING).description("고정 메세지 작성자 아바타 URL")
    ))
    return response
}
