package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.video.*;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Locale;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/1/callbacks/video", produces = MediaType.APPLICATION_JSON_VALUE)
public class CallbackController {
    private static final String VIDEO_LOCKED = "video.locked";
    private static final String MEMBER_NOT_FOUND = "member.not_found";
    private static final String LIVE_NOT_ALLOWED = "video.live_not_allowed";
    private static final String MOTD_UPLOAD_NOT_ALLOWED = "video.motd_upload_not_allowed";

    private final VideoService videoService;
    private final MessageService messageService;
    private final VideoUpdateService videoUpdateService;

    @PostMapping
    public ResponseEntity startVideo(@Valid @RequestBody CallbackStartVideoRequest request,
                                           BindingResult bindingResult,
                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        if (bindingResult.hasErrors()) {
            log.info("{}", bindingResult.getTarget());
        }
        log.info("callback startVideo: {}", request.toString());

        try {
            videoUpdateService.created(request);
        } catch (MemberNotFoundException e) {
            log.error("Invalid UserID: " + request.getUserId());
            throw new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang));
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity updateVideo(@Valid @RequestBody CallbackUpdateVideoRequest request) {
        log.info("callback updateVideo: {}", request.toString());
        try {
            videoUpdateService.updated(request);
        } catch (BadRequestException e) {
            throw new BadRequestException("video_locked", messageService.getMessage(VIDEO_LOCKED, Locale.KOREAN));
        } catch (MemberNotFoundException e) {
            throw new MemberNotFoundException("invalid_user_id", "Invalid user_id: " + request.getUserId());
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public Video deleteVideo(@Valid @RequestBody CallbackDeleteVideoRequest request) {
        log.info("deleteVideo {}", request.toString());
        return videoService.deleteVideo(request.getUserId(), request.getVideoKey());
    }

    @Data
    public static class CallbackStartVideoRequest {
        @NotNull
        String userId;

        @NotNull
        String videoKey;

        @NotNull
        String type;

        String visibility;
        String state;
        Boolean muted = false;
        Boolean locked = false;
        Integer duration = 0;
        String liveKey;
        String outputType;
        String title = "";
        String content = "";
        String originalFilename;
        String url = "";
        String thumbnailPath = "";
        String thumbnailUrl = "";
        String chatRoomId = "";
        String data = "";
        String data2 = "";

        @NotNull
        Date createdAt;
    }

    @Data
    public static class CallbackUpdateVideoRequest {
        @NotNull
        String userId;

        @NotNull
        String videoKey;

        String visibility;
        String state;
        String title;
        String content;
        String originalFilename;
        String url;
        String thumbnailPath;
        String thumbnailUrl;
        String chatRoomId;
        Integer duration;
        String liveKey;
        String outputType;
        String data;
        String data2;
        Integer watchCount;
        Integer heartCount;
        Integer viewCount;

        boolean isFirstOpen;
    }

    @Data
    public static class CallbackDeleteVideoRequest {
        @NotNull
        Long userId;

        @NotNull
        String videoKey;
    }
}