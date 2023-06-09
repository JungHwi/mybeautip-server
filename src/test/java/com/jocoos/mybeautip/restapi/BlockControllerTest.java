package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.domain.member.service.dao.MemberBlockDao;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.restapi.BlockController.BlockMemberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BlockControllerTest extends RestDocsIntegrationTestSupport {

    @MockBean
    private LegacyMemberService legacyMemberService;

    @MockBean
    private MemberBlockDao memberBlockDao;

    @MockBean
    private BlockRepository blockRepository;
    private Block block;

    @BeforeEach
    void init() {
        block = new Block(defaultAdmin.getId(), requestUser);
        block.setId(1L);

        given(legacyMemberService.currentMemberId()).willReturn(defaultAdmin.getId());
        given(memberBlockDao.save(block)).willReturn(block);
    }

    @DisplayName("[POST] /api/1/members/me/blocks - 멤버 블락 성공")
    @Test
    void legacyMemberBlockSuccess() throws Exception {
        BlockMemberRequest request = new BlockMemberRequest();
        request.setMemberId(requestUser.getId());

        MemberRepository memberRepository = Mockito.spy(MemberRepository.class);
        given(memberRepository.findByIdAndDeletedAtIsNull(request.getMemberId())).willReturn(Optional.of(requestUser));
        given(memberBlockDao.getBlockOrElseNewBlock(anyLong(), any())).willReturn(block);

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/1/members/me/blocks")
                        .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(block.getId()));

        resultActions.andDo(document("legacy_member_block",
                requestFields(
                        fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("블락 타겟 멤버 아이디")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("블락 아이디"))));
    }

    @DisplayName("[DELETE] /api/1/members/me/blocks/{blockId} - 멤버 언블락 성공")
    @Test
    void legacyMemberUnblockSuccess() throws Exception {

        final Long blockId = block.getId();
        given(blockRepository.findByIdAndMe(blockId, defaultAdmin.getId())).willReturn(Optional.of(block));

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/1/members/me/blocks/{blockId}", blockId)
                        .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                )
                .andExpect(status().isOk());

        resultActions.andDo(document("legacy_member_unblock",
                    pathParameters(
                            parameterWithName("blockId").description("블락 아이디")
                    )));
    }


}
