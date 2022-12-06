package com.jocoos.mybeautip.domain.video.api.admin;

import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.service.VideoCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminVideoCategoryController {

    private final VideoCategoryService service;

    @GetMapping("/video/category")
    public ResponseEntity<List<VideoCategoryResponse>> getVideoCategories() {
        return ResponseEntity.ok(service.getVideoCategoriesExcludeShapeInfo());
    }
}
