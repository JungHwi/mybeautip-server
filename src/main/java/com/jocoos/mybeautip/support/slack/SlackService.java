package com.jocoos.mybeautip.support.slack;

import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.Delivery;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderInquiry;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
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

  private final VideoGoodsRepository videoGoodsRepository;
  private final RestTemplate restTemplate;

  public SlackService(VideoGoodsRepository videoGoodsRepository,
                      RestTemplate restTemplate) {
    this.videoGoodsRepository = videoGoodsRepository;
    this.restTemplate = restTemplate;
  }

  public void sendForVideo(Video video) {
    String videoType = "BROADCASTED".equals(video.getType()) ? "라이브!" : "#MOTD";
    String message = String.format("*%s(%d)*" +
            "```사용자: %s/%d\n" +
            "영상제목: %s\n" +
            "%s```",
        videoType,
        video.getId(),
        video.getMember().getUsername(),
        video.getMember().getId(),
        video.getTitle(),
        generateRelatedGoodsInfo(video));
    send(message);
  }
  
  private String generateRelatedGoodsInfo(Video video) {
    if (video.getRelatedGoodsCount() == null || video.getRelatedGoodsCount() == 0) {
      return "관련상품: 없음";
    }
    
    StringBuilder sb = new StringBuilder();
    sb.append("관련상품: ").append(video.getRelatedGoodsCount()).append("개");
    List<VideoGoods> goodsList =  videoGoodsRepository.findAllByVideoId(video.getId());
    for (VideoGoods vGoods : goodsList) {
      sb.append("\n - ").append(StringUtils.substring(vGoods.getGoods().getGoodsNm(), 0, 40));
    }
    return sb.toString();
  }

  public void sendForOrder(Order order) {
    Member me = order.getCreatedBy();
    Delivery delivery = order.getDelivery();
    String message = String.format("*주문*" +
            "```order id: %d, member id: %d\n" +
            "관련영상: %s\n" +
            "결제방식: %s\n" +
            "결제금액: %d (배송비 및 할인 적용)" +
            "%s\n" +
            "주문자 정보\n" +
            " - 주문자명: %s\n" +
            " - 휴대폰번호: %s\n" +
            " - 주소: %s\n" +
            " - 이메일: %s\n" +
            "수령자 정보\n" +
            " - 수령자명: %s\n" +
            " - 휴대폰번호: %s\n" +
            " - 주소: %s\n" +
            " - 배송 메세지: %s```",
        order.getId(), order.getCreatedBy().getId(),
        order.getVideoId() == null ? "없음" : order.getVideoId().toString(),
        order.getMethod(),
        order.getPrice(),
        getPurchaseInfo(order.getPurchases()),
        me.getUsername(),
        StringUtils.isEmpty(me.getPhoneNumber()) ? delivery.getPhone() : me.getPhoneNumber(),
        delivery.getRoadAddrPart1() + " " + delivery.getRoadAddrPart2(),
        me.getEmail(),
        delivery.getRecipient(),
        delivery.getPhone(),
        delivery.getRoadAddrPart1() + " " + delivery.getRoadAddrPart2(),
        delivery.getCarrierMessage());
    send(message);
  }
  
  public void sendForOrderCancel(OrderInquiry orderInquiry) {
    String message = String.format("*주문취소*```" +
            "order id: %d, inquiry id: %d\n" +
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
            "order id: %d, inquiry id: %d\n" +
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
            "order id: %d, inquiry id: %d\n" +
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
  
  public void sendForImportGetPaymentException(String impUid, String response) {
    String message = String.format("*아임포트 결제조회 응답이상, payment_id: %s*" +
        "```%s```", impUid, response);
    send(message);
  }
  
  public void sendForImportCancelPaymentException(String impUid, String response) {
    String message = String.format("*아임포트 결제취소 응답이상, payment_id: %s*" +
        "```%s```", impUid, response);
    send(message);
  }
  
  public void sendForImportPaymentMismatch(String impUid, String response) {
    String message = String.format("*아임포트 결제조회 - 결제상태 불일치, payment_id: %s *" +
        "```%s```", impUid, response);
    send(message);
  }
  
  public void sendForImportMerchantIdFormatException(String merchantUid, String impUid) {
    String message = String.format("*아임포트 merchant_uid 확인필요*" +
        "```merchant_uid: %s, imp_uid: %s```", merchantUid, impUid);
    send(message);
  }
  
  public void sendForImportGetTokenFail() {
    String message = String.format("*아임포트 토큰획득 실패*");
    send(message);
  }
  
  private String getPurchaseInfo(List<Purchase> purchases) {
    StringBuilder sb = new StringBuilder();
    for (Purchase purchase : purchases) {
      sb.append(String.format("\n - 상품명: %s, 옵션번호: %s, 수량: %d, 금액: %d원",
          StringUtils.substring(purchase.getGoods().getGoodsNm(), 0, 15),
          StringUtils.isEmpty(purchase.getOptionValue()) ? "없음" : purchase.getOptionId(),
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