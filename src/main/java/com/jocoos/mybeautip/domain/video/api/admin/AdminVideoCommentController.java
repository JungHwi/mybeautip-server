package com.jocoos.mybeautip.domain.video.api.admin;

import com.jocoos.mybeautip.domain.video.dto.AdminVideoCommentResponse;
import com.jocoos.mybeautip.domain.video.service.AdminVideoCommentService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminVideoCommentController {

    private final AdminVideoCommentService service;

    @GetMapping("/video/{videoId}/comment")
    public ResponseEntity<PageResponse<AdminVideoCommentResponse>> getVideoComments(
            @PathVariable Long videoId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, DESC, "createdAt");
        return ResponseEntity.ok(service.getVideoComments(videoId, pageable));
    }

    @PatchMapping("/video/comment/{commentId}/hide")
    public ResponseEntity<IdDto> hideCommunityComment(@PathVariable Long commentId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.hide(commentId, request.isBool())));
    }
}
