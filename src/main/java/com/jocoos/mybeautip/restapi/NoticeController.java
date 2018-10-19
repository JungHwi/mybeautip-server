package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.app.AppInfo;
import com.jocoos.mybeautip.app.AppInfoRepository;
import com.jocoos.mybeautip.devices.NoticeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeController {

  private final NoticeService noticeService;
  private final AppInfoRepository appInfoRepository;

  public NoticeController(NoticeService noticeService,
                          AppInfoRepository appInfoRepository) {
    this.noticeService = noticeService;
    this.appInfoRepository = appInfoRepository;
  }

  @GetMapping
  public ResponseEntity<NoticeResponse> getNotices(@RequestParam("device_os") String deviceOs,
                                            @RequestParam("app_version") String appVersion) {

    List<NoticeInfo> notices = noticeService.findByOs(deviceOs, appVersion)
       .stream().map(input -> new NoticeInfo(input.getType(), input.getMessage()))
       .collect(Collectors.toList());

    NoticeResponse response = new NoticeResponse();
    PageRequest pageable = PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "createdAt"));
    List<AppInfo> list = appInfoRepository.findByOs(deviceOs, pageable);
    response.setLatestVersion((list.size() > 0) ? list.get(0).getVersion() : "");
    response.setContent(notices);
    return new ResponseEntity<>(response, HttpStatus.OK);

  }

  @Data
  public static class NoticeResponse {
    private String latestVersion;
    private List<NoticeInfo> content;
  }
  @AllArgsConstructor
  @Data
  public static class NoticeInfo {
    private String type;
    private String message;
  }
}
