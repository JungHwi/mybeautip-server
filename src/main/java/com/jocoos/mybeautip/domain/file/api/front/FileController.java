package com.jocoos.mybeautip.domain.file.api.front;

import com.jocoos.mybeautip.domain.file.service.FileService;
import com.jocoos.mybeautip.domain.file.validator.annotation.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
@RestController
public class FileController {

    private final FileService service;

    @PostMapping(value = "/1/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> upload(@RequestPart @ValidFile List<MultipartFile> files) {
        return ResponseEntity.ok(service.upload(files));
    }

    @Deprecated(since = "PlanD")
    @PostMapping(value = "/1/community/files", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> uploadFile(@RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(service.upload(files));
    }
}
