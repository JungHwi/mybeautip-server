package com.jocoos.mybeautip.domain.term.api;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.dto.TermDetailResponse;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TermController {

    private final TermService termService;

    @GetMapping("/1/{usedIn}/terms")
    public ResponseEntity<List<TermResponse>> getTermsUsedIn(@PathVariable String usedIn) {
        return ResponseEntity.ok(termService.getTermsUsedIn(TermUsedInType.getBy(usedIn)));
    }

    @GetMapping("1/terms/{termId}")
    public ResponseEntity<TermDetailResponse> getTerm(@PathVariable long termId) {
        return ResponseEntity.ok(termService.getTerm(termId));
    }

}
