package com.jocoos.mybeautip.domain.term.api;

import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TermController {

    private final TermService termService;

    @GetMapping("/1/signup/terms")
    public ResponseEntity<List<TermResponse>> getSignupTerms() {
        return ResponseEntity.ok(termService.getSignupTerms());
    }
}
