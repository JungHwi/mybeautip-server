package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityVoteMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.service.CommunityVoteService;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityVoteControllerTest extends RestDocsTestSupport {

    @MockBean
    private CommunityVoteService communityVoteService;

    @DisplayName("투표 성공 테스트")
    @Test
    void voteSuccess() throws Exception {
        testMemberUtil.defaultTestSetting();

        VoteResponse voteResponse = VoteResponse.builder()
                .id(1L)
                .count(1)
                .fileUrl("test.com").build();

        VoteResponse voteResponse1 = VoteResponse.builder()
                .id(2L)
                .count(0)
                .fileUrl("test2.com")
                .build();

        List<VoteResponse> voteResponses = new ArrayList<>();
        voteResponses.add(voteResponse);
        voteResponses.add(voteResponse1);

        CommunityVoteMemberResponse response = new CommunityVoteMemberResponse(voteResponses, 1L);

        given(communityVoteService.vote(any(), any(), any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/vote/{vote_id}", 1, 1))
                .andExpect(status().isOk())
                .andDo(print());


        resultActions.andDo(document("community_vote",
                        pathParameters(
                                parameterWithName("community_id").description("커뮤니티 ID"),
                                parameterWithName("vote_id").description("투표 ID")
                        ),
                        responseFields(
                                fieldWithPath("votes").type(JsonFieldType.ARRAY).description("투표 파일 List"),
                                fieldWithPath("votes.[].id").type(JsonFieldType.NUMBER).description("투표 파일 아이디"),
                                fieldWithPath("votes.[].file_url").type(JsonFieldType.STRING).description("투표 파일 URL"),
                                fieldWithPath("votes.[].count").type(JsonFieldType.NUMBER).description("투표 수"),
                                fieldWithPath("user_voted").type(JsonFieldType.NUMBER).description("유저가 투표한 파일")
                        )
                )
        );

    }

}
