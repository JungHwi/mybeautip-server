package com.jocoos.mybeautip.domain.term.api;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.converter.MemberTermConverter;
import com.jocoos.mybeautip.domain.term.dto.MemberTermRequest;
import com.jocoos.mybeautip.domain.term.dto.MemberTermResponse;
import com.jocoos.mybeautip.domain.term.dto.TermDetailResponse;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.service.TermService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TermController {

    private final TermService termService;
    private final MemberTermConverter memberTermConverter;

    @GetMapping("/1/{usedIn}/terms")
    public ResponseEntity<List<TermResponse>> getTermsUsedIn(@PathVariable String usedIn) {
        return ResponseEntity.ok(termService.getTermsUsedIn(TermUsedInType.getBy(usedIn)));
    }

    @GetMapping("1/terms/{termId}")
    public ResponseEntity<TermDetailResponse> getTerm(@PathVariable long termId) {
        return ResponseEntity.ok(termService.getTerm(termId));
    }

    @PostMapping("1/terms")
    public ResponseEntity<List<MemberTermResponse>> chooseTerms(
            @RequestBody List<MemberTermRequest> requests) {
        return ResponseEntity
                .ok(termService.chooseTerms(requests));
    }

}
