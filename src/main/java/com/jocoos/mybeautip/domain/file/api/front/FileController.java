package com.jocoos.mybeautip.domain.file.api.front;

import com.jocoos.mybeautip.domain.file.service.FileService;
import com.jocoos.mybeautip.domain.file.validator.annotation.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequiredArgsConstructor
@Validated
@RestController
public class FileController {

    private final FileService service;

    @PostMapping(value = "/api/1/file", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> upload(@RequestPart @ValidFile List<MultipartFile> files) {
        return ResponseEntity.ok(service.upload(files));
    }

    @Deprecated(since = "PlanD")
    @PostMapping(value = "/api/1/community/files", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> uploadCommunityFile(@RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(service.upload(files));
    }

    @Deprecated(since = "PlanD")
    @PostMapping(value = "/admin/event/files", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> uploadEventFile(@RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(service.upload(files));
    }

    @Deprecated(since = "PlanE")
    @PostMapping(value = "/api/1/members/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadAvatar(@RequestBody MultipartFile avatar) {
        return Map.of("avatar_url", service.forceImageUpload(avatar, DEFAULT_AVATAR_URL));
    }
}
