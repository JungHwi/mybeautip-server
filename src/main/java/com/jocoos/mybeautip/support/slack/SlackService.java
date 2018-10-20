package com.jocoos.mybeautip.support.slack;

import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderInquiry;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.report.VideoReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class SlackService {

  @Value("${mybeautip.slack.key}")
  private String slackKey;

  @Value("${mybeautip.slack.channel}")
  private String slackChannel;

  private final RestTemplate restTemplate;

  public SlackService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public void sendForVideo(Video video) {
    String videoType = "BROADCASTED".equals(video.getType()) ? "Live!" : "MOTD";
    String message = String.format("*%s*" +
            "```Id: %d\n" +
            "User: %s/%d\n" +
            "Title: %s\n" +
            "State:%s```",
        videoType,
        video.getId(),
        video.getMember().getUsername(),
        video.getMember().getId(),
        video.getTitle(),
        video.getState());
    send(message);
  }

  public void sendForOrder(Order order) {
    StringBuilder sb = new StringBuilder();
    for (Purchase purchase : order.getPurchases()) {
      sb.append(String.format("\nGoods: %s, Option: %s, Quantity: %d, Price: %d",
          purchase.getGoods().getGoodsNm(),
          purchase.getOptionValue(),
          purchase.getQuantity(),
          purchase.getTotalPrice()));
    }
    String message = String.format("*Order*" +
            "```Id: %d\n" +
            "User: %s/%d\n" +
            "Email: %s\n" +
            "From video: %d\n" +
            "Payment method: %s\n" +
            "Price: %d%s```",
        order.getId(),
        order.getCreatedBy().getUsername(),
        order.getCreatedBy().getId(),
        order.getCreatedBy().getEmail(),
        order.getVideoId(),
        order.getMethod(),
        order.getPrice(),
        sb.toString());
    send(message);
  }

  public void sendForOrderInquiry(OrderInquiry orderInquiry) {
    StringBuilder sb = new StringBuilder();
    for (Purchase purchase : orderInquiry.getOrder().getPurchases()) {
      sb.append(String.format("\nGoods: %s, Option: %s, Quantity: %d, Price: %d",
          purchase.getGoods().getGoodsNm(),
          purchase.getOptionValue(),
          purchase.getQuantity(),
          purchase.getTotalPrice()));
    }

    String message = String.format("*Order Inquiry*```" +
            "Id: %d\n" +
            "State: %d (0: CANCEL, 1: EXCHANGE, 2: REFUND)\n" +
            "User: %s/%d\n" +
            "Reason: %s\n" +
            "Price: %d" +
            "%s```",
        orderInquiry.getId(),
        orderInquiry.getState(),
        orderInquiry.getCreatedBy().getUsername(),
        orderInquiry.getCreatedBy().getId(),
        orderInquiry.getReason(),
        orderInquiry.getOrder().getPrice(),
        sb.toString());
    send(message);
  }

  public void sendForDeleteMember(MemberLeaveLog memberLeaveLog) {
    String message = String.format("*Delete member*" +
            "```User: %s/%d\n" +
            "Email: %s\n" +
            "Link:%d (1:facebook 2:naver 4:kakao)\n" +
            "Reason: %s```",
        memberLeaveLog.getMember().getUsername(),
        memberLeaveLog.getMember().getId(),
        memberLeaveLog.getMember().getEmail(),
        memberLeaveLog.getMember().getLink(),
        memberLeaveLog.getReason());
    send(message);
  }

  public void sendForReportVideo(VideoReport report) {
    String message = String.format("*Video Report*" +
            "```%s/%d reports video %s/%d\n" +
            "Reason: %s```",
        report.getCreatedBy().getUsername(),
        report.getCreatedBy().getId(),
        report.getVideo().getTitle(),
        report.getVideo().getId(),
        report.getReason());
    send(message);
  }

  public void sendForReportMember(Report report) {
    String message = String.format("*Member Report*" +
            "```%s/%d reports member %s/%d\n" +
            "Reason: %s```",
        report.getMe().getUsername(),
        report.getMe().getId(),
        report.getYou().getUsername(),
        report.getYou().getId(),
        report.getReason());
    send(message);
  }

  private void send(String message) {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("https").host("hooks.slack.com").path("services/").path(slackKey).build(true);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");

    JSONObject body = new JSONObject();
    body.put("text", message);

    HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
    restTemplate.exchange(uriComponents.toString(), HttpMethod.POST, request, String.class);
  }
}