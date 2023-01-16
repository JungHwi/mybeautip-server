package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.member.dto.MemberBlockRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;
import static com.jocoos.mybeautip.member.block.BlockStatus.UNBLOCK;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberBlockControllerTest extends RestDocsIntegrationTestSupport {

    @MockBean
    private BlockService blockService;

    @MockBean
    private LegacyMemberService legacyMemberService;

    @DisplayName("[PATCH] /api/1/member/block/ - 멤버 블락 성공")
    @Test
    void memberBlockSuccess() throws Exception {

        Block block = new Block(defaultAdmin.getId(), requestUser);
        MemberBlockRequest request = new MemberBlockRequest(requestUser.getId(), true);
        block.changeStatus(BLOCK);

        given(legacyMemberService.currentMemberId()).willReturn(defaultAdmin.getId());
        given(blockService.changeTargetBlockStatus(defaultAdmin.getId(), request.getTargetId(), request.getIsBlock()))
                .willReturn(block);

        ResultActions resultActions = mockMvc.perform(patch("/api/1/member/block/")
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.member_id").value(defaultAdmin.getId()))
                .andExpect(jsonPath("$.target_id").value(requestUser.getId()))
                .andExpect(jsonPath("$.blocked").value(BLOCK.equals(block.getStatus())));

        restdocs(resultActions);
    }

    @DisplayName("[PATCH] /api/1/members/block/ - 멤버 언블락 성공")
    @Test
    void memberUnblockSuccess() throws Exception {

        Block block = new Block(defaultAdmin.getId(), requestUser);
        MemberBlockRequest request = new MemberBlockRequest(requestUser.getId(), false);
        block.changeStatus(UNBLOCK);

        given(legacyMemberService.currentMemberId()).willReturn(defaultAdmin.getId());
        given(blockService.changeTargetBlockStatus(defaultAdmin.getId(), request.getTargetId(), request.getIsBlock()))
                .willReturn(block);

        ResultActions resultActions = mockMvc.perform(
                        patch("/api/1/member/block/")
                                .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                                .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.member_id").value(defaultAdmin.getId()))
                .andExpect(jsonPath("$.target_id").value(requestUser.getId()))
                .andExpect(jsonPath("$.blocked").value(BLOCK.equals(block.getStatus())));
    }


    @DisplayName("[PATCH] /api/1/members/block/{targetId} - 이미 블락한 멤버의 경우 예외 발생")
    @Test
    void memberBlockFailByAlreadyBlocked() throws Exception {
        Block block = new Block(defaultAdmin.getId(), requestUser);
        MemberBlockRequest request = new MemberBlockRequest(requestUser.getId(), true);

        given(legacyMemberService.currentMemberId()).willReturn(defaultAdmin.getId());

        final String errorMessage = "already blocked, memberId : " + block.getMe() + " targetId :" + block.getYouId();
        given(blockService.changeTargetBlockStatus(defaultAdmin.getId(), request.getTargetId(), request.getIsBlock()))
                .willThrow(new BadRequestException(errorMessage));

        mockMvc.perform(patch("/api/1/member/block/")
                        .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.error_description").value(errorMessage));
    }

    private void restdocs(ResultActions resultActions) throws Exception {
        resultActions.andDo(document("change_target_block_status",
                requestFields(
                        fieldWithPath("target_id").type(JsonFieldType.NUMBER).description("블락 타겟 멤버 아이디"),
                        fieldWithPath("is_block").type(JsonFieldType.BOOLEAN).description("타겟 블락 여부")
                ),
                responseFields(
                        fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("요청 멤버 아이디"),
                        fieldWithPath("target_id").type(JsonFieldType.NUMBER).description("블락한 멤버 아이디"),
                        fieldWithPath("blocked").type(JsonFieldType.BOOLEAN).description("블락 여부"))));
    }

}
