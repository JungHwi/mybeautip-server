package com.jocoos.mybeautip.domain.vod

import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

fun vodListResponse(prefix : String): MutableList<FieldDescriptor> = mutableListOf(
    fieldWithPath(prefix + "id").type(NUMBER).description("VOD 아이디"),
    fieldWithPath(prefix + "is_visible").type(BOOLEAN).description("VOD 공개 여부"),
    fieldWithPath(prefix + "url").type(STRING).description("VOD URL"),
    fieldWithPath(prefix + "title").type(STRING).description("타이틀"),
    fieldWithPath(prefix + "thumbnail_url").type(STRING).description("썸네일 URL"),
    fieldWithPath(prefix + "view_count").type(NUMBER).description("조회수"),
    fieldWithPath(prefix + "heart_count").type(NUMBER).description("하트수"),
    fieldWithPath(prefix + "transcode_at").type(STRING).description("변환일자").attributes(getZonedDateFormat()),
    fieldWithPath(prefix + "category").type(OBJECT).description("카테고리 정보"),
    fieldWithPath(prefix + "category.id").type(NUMBER).description("카테고리 아이디"),
    fieldWithPath(prefix + "category.title").type(STRING).description("카테고리 타이틀"),
    fieldWithPath(prefix + "member").type(OBJECT).description("회원 정보"),
    fieldWithPath(prefix + "member.id").type(NUMBER).description("회원 아이디"),
    fieldWithPath(prefix + "member.email").type(STRING).description("회원 이메일").optional(),
    fieldWithPath(prefix + "member.username").type(STRING).description("회원 닉네임"),
    fieldWithPath(prefix + "member.avatar_url").type(STRING).description("회원 아바타 URL")
)

fun vodListResponseWithRelationInfo(prefix: String) : List<FieldDescriptor> {
    val response = vodListResponse(prefix)
    response.addAll(
        mutableListOf(
        fieldWithPath(prefix + "relation_info").type(OBJECT).description("요청자 연관 정보"),
        fieldWithPath(prefix + "relation_info.is_scrap").type(BOOLEAN).description("요청자 연관 정보 - 스크랩 여부")
    ))
    return response
}

