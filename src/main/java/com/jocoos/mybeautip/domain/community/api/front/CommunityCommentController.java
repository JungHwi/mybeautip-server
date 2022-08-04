package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.service.CommunityCommentService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    @GetMapping("/1/community/{community_id}/comment")
    public ResponseEntity<List<CommunityCommentResponse>> getComments(@PathVariable("community_id") long communityId) {

        return ResponseEntity.ok(communityCommentService.getComments(communityId));
    }

    @GetMapping("/1/community/{community_id}/comment/{comment_id}")
    public ResponseEntity<CommunityCommentResponse> getComment(@PathVariable("community_id") long communityId,
                                                               @PathVariable("comment_id") long commentId) {
        return ResponseEntity.ok(communityCommentService.getComment(communityId, commentId));
    }

    @PostMapping("/1/community/{community_id}/comment")
    public ResponseEntity<CommunityCommentResponse> writeComment(@PathVariable("community_id") long communityId,
                                                                 @RequestBody WriteCommunityCommentRequest request) {
        return ResponseEntity.ok(communityCommentService.write(communityId, request));
    }

    @PutMapping("/1/community/{community_id}/comment/{comment_id}")
    public ResponseEntity<CommunityCommentResponse> editComment(@PathVariable("community_id") long communityId,
                                                                @PathVariable("comment_id") long commentId,
                                                                @RequestBody WriteCommunityCommentRequest request) {

        return ResponseEntity.ok(communityCommentService.edit(communityId, commentId, request));
    }

    @DeleteMapping("/1/community/{community_id}/comment/{comment_id}")
    public ResponseEntity deleteComment(@PathVariable("community_id") long communityId,
                                        @PathVariable("comment_id") long commentId) {

        communityCommentService.delete(communityId, commentId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/1/community/{community_id}/comment/{comment_id}/like")
    public ResponseEntity likeComment(@PathVariable("community_id") long communityId,
                                      @PathVariable("comment_id") long commentId,
                                      @RequestBody BooleanDto isLike) {

        communityCommentService.like(communityId, commentId, isLike.isBool());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/1/community/{community_id}/comment/{comment_id}/report")
    public ResponseEntity reportComment(@PathVariable("community_id") long communityId,
                                        @PathVariable("comment_id") long commentId,
                                        @RequestBody ReportRequest report) {

        communityCommentService.report(communityId, commentId, report);
        return new ResponseEntity(HttpStatus.OK);
    }
}
