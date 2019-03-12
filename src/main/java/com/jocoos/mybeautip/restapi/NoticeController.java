package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.app.AppInfo;
import com.jocoos.mybeautip.app.AppInfoRepository;
import com.jocoos.mybeautip.devices.NoticeService;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.AllArgsConstructor;
import lombok.Data;
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
@RequestMapping(value = "/api/1/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeController {
  
  @Value("${mybeautip.revenue.revenue-ratio-live}")
  private int revenueRatioForLive;
  
  @Value("${mybeautip.revenue.revenue-ratio-vod}")
  private int revenueRatioForVod;
  
  private final NoticeService noticeService;
  private final MessageService messageService;
  private final AppInfoRepository appInfoRepository;

  public NoticeController(NoticeService noticeService,
                          MessageService messageService,
                          AppInfoRepository appInfoRepository) {
    this.noticeService = noticeService;
    this.messageService = messageService;
    this.appInfoRepository = appInfoRepository;
  }

  @GetMapping
  public ResponseEntity<NoticeResponse> getNotices(@RequestParam("device_os") String deviceOs,
                                                   @RequestParam("app_version") String appVersion,
                                                   @RequestParam(defaultValue = "ko") String lang,
                                                   @RequestHeader(value="Accept-Language", defaultValue = "ko") String language) {
    List<NoticeInfo> notices = noticeService.findByOs(deviceOs, appVersion)
       .stream().map(input -> new NoticeInfo(input.getType(),
            messageService.getMessage(input.getMessage(), language)))
       .collect(Collectors.toList());

    NoticeResponse response = new NoticeResponse();
    PageRequest pageable = PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "createdAt"));
    List<AppInfo> list = appInfoRepository.findByOs(deviceOs, pageable);
    response.setLatestVersion((list.size() > 0) ? list.get(0).getVersion() : "");
    response.setRevenueRatio(revenueRatioForLive);
    response.setRevenueRatioForLive(revenueRatioForLive);
    response.setRevenueRatioForVod(revenueRatioForVod);
    response.setContent(notices);
    return new ResponseEntity<>(response, HttpStatus.OK);

  }

  @Data
  public static class NoticeResponse {
    private String latestVersion;
    @Deprecated
    private Integer revenueRatio;
    private Integer revenueRatioForLive;
    private Integer revenueRatioForVod;
    private List<NoticeInfo> content;
  }
  
  @AllArgsConstructor
  @Data
  public static class NoticeInfo {
    private String type;
    private String message;
  }
}
