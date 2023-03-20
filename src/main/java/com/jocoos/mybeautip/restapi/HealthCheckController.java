package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.devices.HealthCheckService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.dto.HealthCheckResponse;
import com.jocoos.mybeautip.restapi.dto.NoticeInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class HealthCheckController {

    private final LegacyMemberService legacyMemberService;
    private final HealthCheckService healthCheckService;
    private final MessageService messageService;

    @Deprecated
    @GetMapping("/1/notices")
    public ResponseEntity<HealthCheckResponse> getNotices(@RequestParam("device_os") String deviceOs,
                                                          @RequestParam("app_version") String appVersion,
                                                          @RequestParam(defaultValue = "ko") String lang,
                                                          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String language) {

        return this.healthCheck(deviceOs, appVersion, lang, language);
    }

    @GetMapping("/1/healthcheck")
    public ResponseEntity<HealthCheckResponse> healthCheck(@RequestParam("device_os") String deviceOs,
                                                           @RequestParam("app_version") String appVersion,
                                                           @RequestParam(defaultValue = "ko") String lang,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String language) {

        deviceOs = deviceOs.trim();

        List<NoticeInfo> notices = healthCheckService.findByOs(deviceOs, appVersion)
                .stream().map(input -> new NoticeInfo(input.getType(),
                        messageService.getMessage(input.getMessage(), language)))
                .collect(Collectors.toList());

        legacyMemberService.updateLastLoggedAt();

        HealthCheckResponse response = new HealthCheckResponse();
        response.setContent(notices);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
