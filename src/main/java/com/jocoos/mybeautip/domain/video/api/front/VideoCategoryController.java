package com.jocoos.mybeautip.domain.video.api.front;

import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.service.dao.VideoCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoCategoryController {
    private final VideoCategoryService service;

    @GetMapping("/1/video/category")
    public ResponseEntity<List<VideoCategoryResponse>> getVideoCategories() {

        return ResponseEntity.ok(service.getVideoCategoryList());
    }
}
