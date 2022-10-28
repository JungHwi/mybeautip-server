package com.jocoos.mybeautip.domain.placard.api.front;

import com.jocoos.mybeautip.domain.placard.dto.PlacardResponse;
import com.jocoos.mybeautip.domain.placard.service.PlacardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PlacardController {

    private final PlacardService placardService;

    @GetMapping("/1/placard")
    public ResponseEntity<List<PlacardResponse>> getPlacardList() {
        List<PlacardResponse> placardResponseList = placardService.getPlacardList();

        return ResponseEntity.ok(placardResponseList);
    }
}
