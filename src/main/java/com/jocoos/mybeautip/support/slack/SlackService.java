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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    String videoType = "BROADCASTED".equals(video.getType()) ? "라이브!" : "#MOTD";
    String message = String.format("*%s(%d)*" +
            "```뷰트리머: %s/%d\n" +
            "영상제목: %s\n" +
            "관련상품: %s```",
        videoType,
        video.getId(),
        video.getMember().getUsername(),
        video.getMember().getId(),
        video.getTitle(),
        video.getData());
    send(message);
  }

  public void sendForOrder(Order order) {
    String message = String.format("*주문*" +
            "```order id: %d\n" +
            "주문자: %s/%d\n" +
            "관련영상: %d\n" +
            "결제방식: %s\n" +
            "결제금액: %d (배송비포함)" +
            "%s```",
        order.getId(),
        order.getCreatedBy().getUsername(),
        order.getCreatedBy().getId(),
        order.getCreatedBy().getEmail(),
        order.getVideoId(),
        order.getMethod(),
        order.getPrice(),
        order.getShippingAmount(),
        getPurchaseInfo(order.getPurchases()));
    send(message);
  }
  
  public void sendForOrderCancel(OrderInquiry orderInquiry) {
    String message = String.format("*주문취소*```" +
            "order id: %d, order inquiry id: %d\n" +
            "주문자: %s/%d\n" +
            "취소이유: %s\n" +
            "결제금액: %d, 결제방식: %s```",
        orderInquiry.getOrder().getId(),
        orderInquiry.getId(),
        orderInquiry.getCreatedBy().getUsername(),
        orderInquiry.getCreatedBy().getId(),
        orderInquiry.getReason(),
        orderInquiry.getOrder().getPrice(),
        orderInquiry.getOrder().getPayment().getMethod());
    send(message);
  }
  
  public void sendForOrderCancelByAdmin(OrderInquiry orderInquiry) {
    String message = String.format("*관리자에의한 주문취소*```" +
            "order id: %d, order inquiry id: %d\n" +
            "주문자: %s/%d\n" +
            "결제금액: %d, 결제방식: %s```",
        orderInquiry.getOrder().getId(),
        orderInquiry.getId(),
        orderInquiry.getCreatedBy().getUsername(),
        orderInquiry.getCreatedBy().getId(),
        orderInquiry.getOrder().getPrice(),
        orderInquiry.getOrder().getPayment().getMethod());
    send(message);
  }
  
  public void SendForOrderExchangeOrReturn(OrderInquiry orderInquiry) {
    String message = String.format("*주문문의*```" +
            "order id: %d, order inquiry id: %d\n" +
            "문의종류: %d (1: EXCHANGE, 2: REFUND)\n" +
            "주문자: %s/%d\n" +
            "문의내용: %s\n" +
            "현재상태: %s```",
        orderInquiry.getOrder().getId(),
        orderInquiry.getId(),
        orderInquiry.getState(),
        orderInquiry.getCreatedBy().getUsername(),
        orderInquiry.getCreatedBy().getId(),
        orderInquiry.getReason(),
        orderInquiry.getOrder().getStatus());
    send(message);
  }

  public void sendForDeleteMember(MemberLeaveLog memberLeaveLog) {
    String message = String.format("*회원탈퇴*" +
            "```사용자: %s/%d\n" +
            "Link:%d (1:facebook 2:naver 4:kakao)\n" +
            "탈퇴이유: %s```",
        memberLeaveLog.getMember().getUsername(),
        memberLeaveLog.getMember().getId(),
        memberLeaveLog.getMember().getEmail(),
        memberLeaveLog.getMember().getLink(),
        memberLeaveLog.getReason());
    send(message);
  }

  public void sendForReportVideo(VideoReport report) {
    String message = String.format("*영상신고*" +
            "```%s/%d (이)가 %s/%d 영상을 신고함\n" +
            "신고이유: %s```",
        report.getCreatedBy().getUsername(),
        report.getCreatedBy().getId(),
        report.getVideo().getTitle(),
        report.getVideo().getId(),
        report.getReason());
    send(message);
  }

  public void sendForReportMember(Report report) {
    String message = String.format("*회원신고*" +
            "```%s/%d (이)가 %s/%d 회원을 신고함\n" +
            "신고이유: %s```",
        report.getMe().getUsername(),
        report.getMe().getId(),
        report.getYou().getUsername(),
        report.getYou().getId(),
        report.getReason());
    send(message);
  }
  
  public void sendForImportGetTokenFail() {
    String message = String.format("*아임포트 확인필요*" +
        "```토큰획득 실패```");
    send(message);
  }
  
  public void sendForImportGetPaymentFail(String impUid) {
    String message = String.format("*아임포트 확인필요*" +
        "```결제조회 실패, payment_id: %s```", impUid);
    send(message);
  }
  
  public void sendForImportPaymentException(String impUid) {
    String message = String.format("*아임포트 확인필요*" +
        "```결제상태 확인필요, payment_id: %s```", impUid);
    send(message);
  }
  
  public void sendForImportPaymentMismatch(String impUid) {
    String message = String.format("*아임포트 확인필요*" +
        "```결제상태 불일치 - 확인필요, payment_id: %s```", impUid);
    send(message);
  }
  
  public void sendForImportMerchantIdFormatException(String merchantUid, String impUid) {
    String message = String.format("*아임포트 확인필요*" +
        "```merchant_uid 확인필요, merchant_uid: %s, imp_uid: %s```", merchantUid, impUid);
    send(message);
  }
  
  private String getPurchaseInfo(List<Purchase> purchases) {
    StringBuilder sb = new StringBuilder();
    for (Purchase purchase : purchases) {
      sb.append(String.format("\n - 상품명: %s..., 옵션번호: %s, 수량: %d, 금액: %d원 ((상품판매가+옵션가)x수량)",
          StringUtils.substring(purchase.getGoods().getGoodsNm(), 0, 10),
          StringUtils.isEmpty(purchase.getOptionValue()) ? "없음" : purchase.getOptionValue(),
          purchase.getQuantity(),
          purchase.getTotalPrice()));
    }
    return sb.toString();
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