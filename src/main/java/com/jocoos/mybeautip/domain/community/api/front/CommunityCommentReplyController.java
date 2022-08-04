package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentReplyResponse;
import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.service.CommunityCommentReplyService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityCommentReplyController {

    private final CommunityCommentReplyService communityCommentReplyService;

    @GetMapping("/1/community/{community_id}/comment/{comment_id}/reply")
    public ResponseEntity<List<CommunityCommentReplyResponse>> getReplies(@PathVariable("community_id") long communityId,
                                                                          @PathVariable("comment_id") long commentId) {

        return ResponseEntity.ok(communityCommentReplyService.getReplies(communityId, commentId));
    }

    @GetMapping("/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}")
    public ResponseEntity<CommunityCommentReplyResponse> getReply(@PathVariable("community_id") long communityId,
                                                                  @PathVariable("comment_id") long commentId,
                                                                  @PathVariable("reply_id") long replyId) {
        return ResponseEntity.ok(communityCommentReplyService.getReply(communityId, commentId, replyId));
    }

    @PostMapping("/1/community/{community_id}/comment/{comment_id}/reply")
    public ResponseEntity<CommunityCommentReplyResponse> writeReply(@PathVariable("community_id") long communityId,
                                                                 @PathVariable("comment_id") long commentId,
                                                                 @RequestBody WriteCommunityCommentRequest request) {



        return ResponseEntity.ok(communityCommentReplyService.write(request));
    }

    @PutMapping("/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}")
    public ResponseEntity<CommunityCommentReplyResponse> editReply(@PathVariable("community_id") long communityId,
                                                                     @PathVariable("comment_id") long commentId,
                                                                     @PathVariable("reply_id") long replyId,
                                                                     @RequestBody WriteCommunityCommentRequest request) {

        return ResponseEntity.ok(communityCommentReplyService.edit(replyId, request));
    }

    @DeleteMapping("/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}")
    public ResponseEntity deleteReply(@PathVariable("community_id") long communityId,
                                        @PathVariable("comment_id") long commentId,
                                        @PathVariable("reply_id") long replyId) {

        communityCommentReplyService.delete(communityId, commentId, replyId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}/like")
    public ResponseEntity likeReply(@PathVariable("community_id") long communityId,
                                      @PathVariable("comment_id") long commentId,
                                      @PathVariable("reply_id") long replyId,
                                      @RequestBody BooleanDto isLike) {

        communityCommentReplyService.like(communityId, commentId, replyId, isLike.isBool());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}/report")
    public ResponseEntity reportReply(@PathVariable("community_id") long communityId,
                                        @PathVariable("comment_id") long commentId,
                                        @PathVariable("reply_id") long replyId,
                                        @RequestBody ReportRequest report) {

        communityCommentReplyService.report(communityId, commentId, replyId, report);
        return new ResponseEntity(HttpStatus.OK);
    }
}
