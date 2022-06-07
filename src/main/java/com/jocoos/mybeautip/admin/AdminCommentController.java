package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.comment.CommentService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/comments")
public class AdminCommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    public AdminCommentController(CommentService commentService,
                                  CommentRepository commentRepository) {
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    @PatchMapping("/{id:.+}")
    public void lockComment(@PathVariable Long id,
                            @RequestBody UpdateCommentRequest request) {
        log.debug("request: {}", request);

        if (request != null && request.getLocked()) {
            commentRepository.findById(id)
                    .map(comment -> {
                        if (comment.getLocked()) {
                            throw new BadRequestException("already_locked", "Comment already locked");
                        }
                        commentService.lockComment(comment);
                        return Optional.empty();
                    })
                    .orElseThrow(() -> new NotFoundException("comment_not_found", "Comment not found, id: " + id));
        } else {
            throw new BadRequestException("invalid_request_body", "Invalid request body");
        }
    }

    @Data
    private static class UpdateCommentRequest {
        private Boolean locked;
    }
}
