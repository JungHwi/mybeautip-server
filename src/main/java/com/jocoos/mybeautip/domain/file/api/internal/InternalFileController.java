package com.jocoos.mybeautip.domain.file.api.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.jocoos.mybeautip.domain.file.service.FileService;
import com.jocoos.mybeautip.domain.file.validator.annotation.ValidFile;

import lombok.RequiredArgsConstructor;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequiredArgsConstructor
@Validated
@RestController
public class InternalFileController {

    private final FileService service;

    @PostMapping(value = "/internal/1/file", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> upload(@RequestPart @ValidFile List<MultipartFile> files) {
        return ResponseEntity.ok(service.upload(files));
    }
}
