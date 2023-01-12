package com.jocoos.mybeautip.domain.video.api.admin;

import com.jocoos.mybeautip.domain.video.dto.AdminVideoCommentResponse;
import com.jocoos.mybeautip.domain.video.dto.PatchVideoCommentRequest;
import com.jocoos.mybeautip.domain.video.dto.WriteVideoCommentRequest;
import com.jocoos.mybeautip.domain.video.service.AdminVideoCommentService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminVideoCommentController {

    private final AdminVideoCommentService service;

    @PostMapping("/video/{videoId}/comment")
    public ResponseEntity<AdminVideoCommentResponse> write(@PathVariable Long videoId,
                                                           @RequestBody @Valid WriteVideoCommentRequest request) {
        AdminVideoCommentResponse response = service.write(videoId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build()
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/video/{videoId}/comment/{commentId}")
    public ResponseEntity<IdDto> edit(@PathVariable Long videoId,
                                      @PathVariable Long commentId,
                                      @CurrentMember MyBeautipUserDetails userDetails,
                                      @RequestBody @Valid PatchVideoCommentRequest request) {
        return ResponseEntity.ok(new IdDto(service.edit(videoId, commentId, userDetails.getMember(), request.getContents())));
    }

    @GetMapping("/video/{videoId}/comment")
    public ResponseEntity<PageResponse<AdminVideoCommentResponse>> getVideoComments(
            @PathVariable Long videoId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, DESC, "createdAt");
        return ResponseEntity.ok(service.getVideoComments(videoId, pageable));
    }

    @PatchMapping("/video/{videoId}/comment/{commentId}/hide")
    public ResponseEntity<IdDto> hideCommunityComment(@PathVariable Long videoId,
                                                      @PathVariable Long commentId,
                                                      @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.hide(videoId, commentId, request.isBool())));
    }
}
