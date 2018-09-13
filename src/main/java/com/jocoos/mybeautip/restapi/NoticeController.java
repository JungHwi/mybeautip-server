package com.jocoos.mybeautip.restapi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.devices.NoticeService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeController {

  private final NoticeService noticeService;

  public NoticeController(NoticeService noticeService) {
    this.noticeService = noticeService;
  }

  @GetMapping
  public ResponseEntity<Notices> getNotices(@RequestParam("device_os") String deviceOs,
                                            @RequestParam("app_version") String appVersion) {

    List<NoticeInfo> notices = noticeService.findByOs(deviceOs, appVersion)
       .stream().map(input -> new NoticeInfo(input.getType(), input.getMessage()))
       .collect(Collectors.toList());

    return new ResponseEntity<>(new Notices(notices), HttpStatus.OK);

  }

  @AllArgsConstructor
  @Data
  public static class NoticeInfo {
    private String key;
    private String message;
  }

  @EqualsAndHashCode(callSuper = false)
  @Data
  public static class Notices extends ArrayList<NoticeInfo> {
    public Notices(List<NoticeInfo> notices) {
      super(notices);
    }
  }
}
