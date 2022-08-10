package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;
import static com.jocoos.mybeautip.member.block.BlockStatus.UNBLOCK;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BlockControllerTest extends RestDocsTestSupport {

    @MockBean
    private BlockService blockService;

    @MockBean
    private LegacyMemberService legacyMemberService;

    private final long memberId = 1L;
    private final long targetId = 2L;
    private Block block;


    @BeforeEach
    void init() {
        final String targetSocialId = "123123123456";
        final Member targetMember = super.defaultMember(targetId, targetSocialId);
        block = new Block(1L, targetMember);

        given(legacyMemberService.currentMemberId()).willReturn(memberId);
    }


    @DisplayName("[PATCH] /api/1/members/block/{targetId} - 멤버 블락 성공")
    @Test
    void memberBlockSuccess() throws Exception {

        block.changeStatus(BLOCK);
        given(blockService.blockMember(memberId, targetId)).willReturn(block);

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.
                        patch("/api/1/members/block/{targetId}", targetId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.member_id").value(memberId))
                .andExpect(jsonPath("$.target_id").value(targetId))
                .andExpect(jsonPath("$.blocked").value(BLOCK.equals(block.getStatus())));

        restdocs(resultActions, "block_target_member", "블락할 멤버 아이디");
    }

    @DisplayName("[PATCH] /api/1/members/unblock/{targetId} - 멤버 언블락 성공")
    @Test
    void memberUnblockSuccess() throws Exception {

        block.changeStatus(UNBLOCK);
        given(blockService.unblockMember(memberId, targetId)).willReturn(block);

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.
                        patch("/api/1/members/unblock/{targetId}", targetId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.member_id").value(memberId))
                .andExpect(jsonPath("$.target_id").value(targetId))
                .andExpect(jsonPath("$.blocked").value(BLOCK.equals(block.getStatus())));

        restdocs(resultActions, "unblock_target_member", "언블락할 멤버 아이디");

    }


    @DisplayName("[PATCH] /api/1/members/block/{targetId} - 이미 블락한 멤버의 경우 예외 발생")
    @Test
    void memberBlockFailByAlreadyBlocked() throws Exception {
        final String errorMessage = "already blocked, memberId : " + block.getMe() + " targetId :" + block.getYouId();
        given(blockService.blockMember(memberId, targetId))
                .willThrow(new BadRequestException(errorMessage));

        mockMvc.perform(RestDocumentationRequestBuilders.
                        patch("/api/1/members/block/{targetId}", targetId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.error_description").value(errorMessage));
    }

    private void restdocs(ResultActions resultActions, String documentName, String targetIdDescription) throws Exception {
        resultActions.andDo(document(documentName,
                pathParameters(
                        parameterWithName("targetId").description(targetIdDescription)
                ),
                responseFields(
                        fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("요청 멤버 아이디"),
                        fieldWithPath("target_id").type(JsonFieldType.NUMBER).description("블락한 멤버 아이디"),
                        fieldWithPath("blocked").type(JsonFieldType.BOOLEAN).description("블락 여부"))));
    }
}
