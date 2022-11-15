package com.jocoos.mybeautip.domain.placard.api.admin;

import com.jocoos.mybeautip.domain.placard.dto.PlacardRequest;
import com.jocoos.mybeautip.domain.placard.service.AdminPlacardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminPlacardController {

    private final AdminPlacardService service;

    @PostMapping("/placard")
    public void createPlacard(@RequestBody @Valid PlacardRequest request) {
        service.create(request);
    }
}
