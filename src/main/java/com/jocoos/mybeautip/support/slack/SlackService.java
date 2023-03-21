package com.jocoos.mybeautip.support.slack;

import com.jocoos.mybeautip.admin.Dates;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderInquiry;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.restapi.ScheduleController;
import com.jocoos.mybeautip.schedules.Schedule;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SlackService {

    private final RestTemplate restTemplate;
    @Value("${mybeautip.slack.key}")
    private String slackKey;
    @Value("${mybeautip.slack.order-key}")
    private String orderSlackKey;

    public void sendForOrder(Order order) {
        String message = String.format("*주문(%d)*```주문자: %s/%d%n관련영상: %s%n결제금액: %d, 결제방식: %s%s```",
                order.getId(),
                order.getCreatedBy().getUsername(), order.getCreatedBy().getId(),
                order.getVideoId() == null ? "없음" : order.getVideoId().toString(),
                order.getPrice(),
                order.getMethod(),
                getPurchaseInfo(order.getPurchases()));
        sendToOrderChannel(message);
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
        sendToOrderChannel(message);
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
        sendToOrderChannel(message);
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
        sendToOrderChannel(message);
    }

    public void sendForImportRequestBillingException(String merchantId, String response) {
        String message = String.format("*아임포트 결제요청 응답이상, merchant_id: %s*" +
                "```%s```", merchantId, response);
        send(message);
    }

    public void sendForImportGetCardInfoException(Long billingId, String response) {
        String message = String.format("*아임포트 카드정보조회 응답이상, billing_id: %s*" +
                "```%s```", billingId, response);
        send(message);
    }

    public void sendForImportDeleteBillingInfoException(Long billingId, String response) {
        String message = String.format("*아임포트 빌링키정보삭제 응답이상, billing_id: %s*" +
                "```%s```", billingId, response);
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
        String message = "*아임포트 토큰획득 실패*";
        send(message);
    }

    public void sendStatsForLiveEnded(long videoId, String statMessage) {
        String message = String.format("*라이브(%d) 종료*" +
                "```%s```", videoId, statMessage);
        send(message);
    }

    public void sendPointToMember(MemberPoint memberPoint) {
        String details = String.format("%s/%d - 포인트: +%s, 유효기간: %s", memberPoint.getMember().getUsername(), memberPoint.getMember().getId(),
                memberPoint.getFormattedPoint(), Dates.toString(memberPoint.getExpiryAt(), ZoneId.of("Asia/Seoul")));
        String message = String.format("*포인트(%d) 지급*" +
                "```%s```", memberPoint.getPoint(), details);

        log.debug("{}", message);
        send(message);
    }

    public void sendUsedCouponUse(MemberCoupon memberCoupon) {
        String details = String.format("%s/%d - 쿠폰: -%s, 유효기간: %s", memberCoupon.getMember().getUsername(), memberCoupon.getMember().getId(),
                memberCoupon.getCoupon().getDescription(), Dates.toString(memberCoupon.getExpiryAt(), ZoneId.of("Asia/Seoul")));
        String message = String.format("*사용한 쿠폰(%d)을 사용함 *" +
                "```%s```", memberCoupon.getId(), details);
        log.debug("{}", message);
        send(message);
    }

    public void sendForSchedule(Schedule schedule) {
        String message = String.format("*스케줄 등록*" +
                        "```사용자: %s/%d\n" +
                        "제목: %s\n" +
                        "시간: %s```",
                schedule.getCreatedBy().getUsername(),
                schedule.getCreatedBy().getId(),
                schedule.getTitle(),
                Dates.toString(schedule.getStartedAt(), ZoneId.of("Asia/Seoul")));
        send(message);
    }

    public void sendForUpdatingSchedule(Schedule prevSchedule, ScheduleController.UpdateScheduleRequest updatedSchedule) {
        String message = String.format("*스케줄 변경*" +
                        "```사용자: %s/%d\n" +
                        "제목: %s -> %s\n" +
                        "시간: %s -> %s```",
                prevSchedule.getCreatedBy().getUsername(),
                prevSchedule.getCreatedBy().getId(),
                prevSchedule.getTitle(),
                updatedSchedule.getTitle(),
                Dates.toString(prevSchedule.getStartedAt(), ZoneId.of("Asia/Seoul")),
                Dates.toString(updatedSchedule.getStartedAt(), ZoneId.of("Asia/Seoul")));
        send(message);
    }

    public void makeVideoPublic(Video v) {
        String title = String.format("비공개 컨텐츠(%d)가 공개되었습니다.", v.getId());
        String message = String.format("사용자: %s / %d, 비디오 키: %s, 영상제목: %s, visibility: %s",
            v.getMember().getUsername(), v.getMember().getId(), v.getVideoKey(),
            v.getTitle(), v.getVisibility());;

        SlackMessageFormat slackMessage = new SlackMessageFormat(title, message);
        send(slackMessage);
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

    public void send(String message) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https").host("hooks.slack.com").path("services/").path(slackKey).build(true);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");

        JSONObject body = new JSONObject();
        body.put("text", message);

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        try {
            restTemplate.exchange(uriComponents.toString(), HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            log.warn("Send slack message throws exception: " + e.getMessage());
            // Do not throw exception
        }
    }

    public void send(SlackMessageFormat slackMessageFormat) {
        if (slackMessageFormat != null) {
            send(slackMessageFormat.toString());
        }
    }

    private void sendToOrderChannel(String message) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https").host("hooks.slack.com").path("services/").path(orderSlackKey).build(true);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");

        JSONObject body = new JSONObject();
        body.put("text", message);

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        try {
            restTemplate.exchange(uriComponents.toString(), HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            log.warn("Send slack message throws exception: " + e.getMessage());
            // Do not throw exception
        }
    }
}
