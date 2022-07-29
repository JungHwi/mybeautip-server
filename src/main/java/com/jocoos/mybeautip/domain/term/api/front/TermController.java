package com.jocoos.mybeautip.domain.term.api.front;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.dto.TermDetailResponse;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.service.TermService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TermController {

    private final TermService termService;

//    @GetMapping("/1/{usedIn}/terms")
    public ResponseEntity<List<TermResponse>> getTermsUsedIn(@PathVariable String usedIn) {
        return ResponseEntity.ok(termService.getTermsUsedIn(TermUsedInType.getBy(usedIn)));
    }

//    @GetMapping("/1/terms/{termId}")
    public ResponseEntity<TermDetailResponse> getTermDetail(@PathVariable long termId) {
        return ResponseEntity.ok(termService.getTermDetail(termId));
    }
}
