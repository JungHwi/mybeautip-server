package com.jocoos.mybeautip.domain.video.api.admin;

import com.jocoos.mybeautip.domain.video.code.VideoStatus;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoResponse;
import com.jocoos.mybeautip.domain.video.service.AdminVideoService;
import com.jocoos.mybeautip.domain.video.vo.AdminVideoSearchCondition;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.dto.single.SortOrderDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.video.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.jocoos.mybeautip.global.code.SearchDomain.VIDEO;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminVideoController {

    private final AdminVideoService service;

    @GetMapping("/video")
    public ResponseEntity<PageResponse<AdminVideoResponse>> getVideos(
            @RequestParam(required = false) VideoStatus status,
            @RequestParam(name = "category_id", required = false) Integer categoryId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "startedAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String order,
            @RequestParam(required = false) String search,
            @RequestParam(name = "start_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(name = "end_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
            @RequestParam(name = "is_reported", required = false) Boolean isReported,
            @RequestParam(name = "is_top_fix", required = false) Boolean isTopFix,
            @RequestParam(name = "is_recommended", required = false) Boolean isRecommended,
            @RequestParam(required = false) Visibility visibility) {

        SearchOption searchOption = SearchOption.builder()
                .domain(VIDEO)
                .searchQueryString(search)
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .isReported(isReported)
                .isTopFix(isTopFix)
                .build();

        AdminVideoSearchCondition condition = AdminVideoSearchCondition.builder()
                .status(status)
                .categoryId(categoryId)
                .visibility(visibility)
                .isRecommended(isRecommended)
                .pageable(PageRequest.of(page - 1, size, Direction.fromString(order), sort))
                .searchOption(searchOption)
                .build();

        return ResponseEntity.ok(service.getVideos(condition));
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<AdminVideoResponse> getVideo(@PathVariable Long videoId) {
        return ResponseEntity.ok(service.getVideo(videoId));
    }

    @PatchMapping("/video/{videoId}/hide")
    public ResponseEntity<IdDto> changeVisibilityOfVideo(@PathVariable Long videoId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.changeVisibility(videoId, request.isBool())));
    }

    @PatchMapping("/video/{videoId}/recommend")
    public ResponseEntity<IdDto> recommendVideo(@PathVariable Long videoId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.recommend(videoId, request.isBool())));
    }

    @DeleteMapping("/video/{videoId}")
    public ResponseEntity<IdDto> deleteVideo(@PathVariable Long videoId) {
        return ResponseEntity.ok(new IdDto(service.delete(videoId)));
    }

    @PatchMapping("/video/{videoId}/fix")
    public ResponseEntity<IdDto> topFixVideo(@PathVariable Long videoId, @RequestBody BooleanDto isTopFix) {
        return ResponseEntity.ok(new IdDto(service.topFix(videoId, isTopFix.isBool())));
    }

    @PatchMapping("/video/order")
    public ResponseEntity<SortOrderDto> changeOrderOfVideo(@RequestBody SortOrderDto request) {
        return ResponseEntity.ok(new SortOrderDto(service.arrange(request.getSortedIds())));
    }
}
