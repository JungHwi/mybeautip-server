package com.jocoos.mybeautip.domain.community.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class WriteCommunityRequest {

    private Long categoryId;

    private Long eventId;

    private String title;

    private String contents;

    private List<MultipartFile> files;
}
