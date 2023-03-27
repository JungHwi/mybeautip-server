package com.jocoos.mybeautip.domain.community.api.internal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;

import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.service.CommunityCommentService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalCommunityCommentController {

    private final CommunityCommentService communityCommentService;

    @GetMapping("/1/community/{community_id}/comment")
    public ResponseEntity<CursorResultResponse<CommunityCommentResponse>> getComments(@PathVariable("community_id") long communityId,
                                                                                      @RequestParam(value = "parent_id", required = false) Long parentId,
                                                                                      @RequestParam(required = false) Long cursor,
                                                                                      @RequestParam(required = false, defaultValue = "20") int size,
                                                                                      @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction) {
        if (cursor == null) {
            if (direction.isAscending()) {
                cursor = Long.MIN_VALUE;
            } else {
                cursor = Long.MAX_VALUE;
            }
        }

        SearchCommentRequest request = SearchCommentRequest.builder()
                .communityId(communityId)
                .parentId(parentId)
                .cursor(cursor)
                .pageable(PageRequest.of(0, size, Sort.by(direction, "id")))
                .build();

        List<CommunityCommentResponse> response = communityCommentService.getComments(request);
        CursorResultResponse<CommunityCommentResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/1/community/{community_id}/comment/{comment_id}")
    public ResponseEntity<CommunityCommentResponse> getComment(@PathVariable("community_id") long communityId,
                                                               @PathVariable("comment_id") long commentId) {
        return ResponseEntity.ok(communityCommentService.getComment(communityId, commentId));
    }

    @PostMapping("/1/community/{community_id}/comment")
    public ResponseEntity<CommunityCommentResponse> writeComment(@CurrentMember MyBeautipUserDetails userDetails,
                                                                 @PathVariable("community_id") long communityId,
                                                                 @RequestBody WriteCommunityCommentRequest request) {
        request.setCommunityId(communityId);
        request.setMember(userDetails.getMember());
        return ResponseEntity.ok(communityCommentService.write(request));
    }

    @PutMapping("/1/community/{community_id}/comment/{comment_id}")
    public ResponseEntity<CommunityCommentResponse> editComment(@CurrentMember MyBeautipUserDetails userDetails,
                                                                @PathVariable("community_id") long communityId,
                                                                @PathVariable("comment_id") long commentId,
                                                                @RequestBody @Valid EditCommunityCommentRequest request) {
        request.setMember(userDetails.getMember());
        request.setCommunityId(communityId);
        request.setCommentId(commentId);
        return ResponseEntity.ok(communityCommentService.edit(request));
    }

    @DeleteMapping("/1/community/{community_id}/comment/{comment_id}")
    public ResponseEntity deleteComment(@PathVariable("community_id") long communityId,
                                        @PathVariable("comment_id") long commentId) {

        communityCommentService.delete(communityId, commentId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/1/community/{community_id}/comment/{comment_id}/like")
    public ResponseEntity<LikeResponse> likeComment(@PathVariable("community_id") long communityId,
                                                    @PathVariable("comment_id") long commentId,
                                                    @RequestBody BooleanDto isLike) {

        LikeResponse result = communityCommentService.like(commentId, isLike.isBool());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/1/community/{community_id}/comment/{comment_id}/report")
    public ResponseEntity<ReportResponse> reportComment(@PathVariable("community_id") long communityId,
                                                        @PathVariable("comment_id") long commentId,
                                                        @RequestBody ReportRequest report) {

        ReportResponse result = communityCommentService.report(commentId, report);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/1/community/{community_id}/comment/{comment_id}/report")
    public ResponseEntity<ReportResponse> isReportCommunity(@PathVariable("community_id") String communityId,
                                                            @PathVariable(name = "comment_id") long commentId) {
        ReportResponse result = communityCommentService.isReport(commentId);

        return ResponseEntity.ok(result);
    }
}
