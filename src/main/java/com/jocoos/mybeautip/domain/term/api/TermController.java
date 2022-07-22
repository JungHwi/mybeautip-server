package com.jocoos.mybeautip.domain.term.api;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TermController {

    private final TermService termService;

    @GetMapping("/1/terms/{usedIn}")
    public ResponseEntity<List<TermResponse>> getTermsUsedIn(@PathVariable TermUsedInType usedIn) {
        return ResponseEntity.ok(termService.getTermsUsedIn(usedIn));
    }


}
