package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.app.AppInfo;
import com.jocoos.mybeautip.app.AppInfoRepository;
import com.jocoos.mybeautip.devices.NoticeService;
import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.domain.popup.service.PopupService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.dto.NoticeInfo;
import com.jocoos.mybeautip.restapi.dto.NoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/1/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeController {

    private final LegacyMemberService legacyMemberService;
    private final NoticeService noticeService;
    private final MessageService messageService;
    private final AppInfoRepository appInfoRepository;
    private final PopupService popupService;
    @Value("${mybeautip.revenue.revenue-ratio-live}")
    private int revenueRatioForLive;
    @Value("${mybeautip.revenue.revenue-ratio-vod}")
    private int revenueRatioForVod;

    @GetMapping
    public ResponseEntity<NoticeResponse> getNotices(@RequestParam("device_os") String deviceOs,
                                                     @RequestParam("app_version") String appVersion,
                                                     @RequestParam(defaultValue = "ko") String lang,
                                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String language) {
        deviceOs = deviceOs.trim();
        Member member = legacyMemberService.currentMember();
        List<PopupResponse> popupList = popupService.getPopup(member);

        List<NoticeInfo> notices = noticeService.findByOs(deviceOs, appVersion)
                .stream().map(input -> new NoticeInfo(input.getType(),
                        messageService.getMessage(input.getMessage(), language)))
                .collect(Collectors.toList());

        legacyMemberService.updateLastLoggedAt();

        NoticeResponse response = new NoticeResponse();
        PageRequest pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<AppInfo> list = appInfoRepository.findByOs(deviceOs, pageable);
        response.setLatestVersion((list.size() > 0) ? list.get(0).getVersion() : "");
        response.setContent(notices);
        response.setPopupList(popupList);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
