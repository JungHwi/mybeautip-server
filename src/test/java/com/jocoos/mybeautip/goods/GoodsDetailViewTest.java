package com.jocoos.mybeautip.goods;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoodsDetailViewTest {

  public static void main(String[] args) {
    String html = "<div class=\"js_goods_description\">" +
       "<div align=\"center\" style=\"color:#fff; font-size:16px; font-weight:normal; margin:24px 0; padding:32px; background: #eee url(https://mybeautip.s3.ap-northeast-2.amazonaws.com/banner/20180706_timedeal01.png) no-repeat center center; background-size: cover;  text-align: center;\">" +
       "<p>타임딜은 마이뷰팁 회원만 구매 가능합니다. </p>" +
       "<p>선착순으로 진행되니까 품절 되기 전에 미리미리 회원가입 잊지 마세요! </p>\n" +
       "<p><a href=\"/member/join_method.php\" style=\"color:#fff; font-weight:normal; text-decoration:underline\">회원가입</a></p>" +
       "</div>\n" + "\n" +
       "<div align=\"center\" style=\"font-size:14px; padding:20px 0;\">" +
       "공급사의 제품 입고 스케줄에 따라 배송이 다소 지연될 수 있습니다. 주문량이 많을 경우 주문 순서에 맞춰 순차적으로 배송이 진행되오니 양해 해주시기 바랍니다" +
       "</div>\n" +
       "<div class=\"mybeautip-video-container player-container\" align=\"center\">\n" +
       "<video width=\"100%\" height=\"100%\" id=\"video-player\" class=\"embed-responsive-item\" controls=\"\" preload=\"auto\" x-webkit-ariplay=\"allow\" webkit-playsinline=\"allow\" playsinline=\"\" src=\"https://flipflop-dev.s3.ap-northeast-2.amazonaws.com/testapp2/videos/11178/vod.mp4\" poster=\"https://s3.ap-northeast-2.amazonaws.com/flipflop-dev/testapp2/videos/11178/11178.png\"></video>\n" +
       "</div>\n" + "\n" +
       "<p><a href=\"/member/join_method.php\" style=\"color:#fff; font-weight:normal; text-decoration:underline\">회원가입</a></p>\n" +
       "<div align=\"center\">" +
       "<img src=\"https://mybeautip.s3.ap-northeast-2.amazonaws.com/supplier/header_hitomi.png\" style=\"max-width: 100%; height: auto;\">" +
       "</div>" +
       "    <div align=\"center\">" +
       "<img src=\"https://mybeautip.s3.ap-northeast-2.amazonaws.com/supplier/header_hitomi_ysl.png\" style=\"max-width: 100%; height: auto;\">" +
       "</div>" +
       "<div style=\"text-align: center;\">" +
       "<p style=\"font-size: medium\">매트 립의 공식을 깨다</p>" +
       "<p>입생로랑의 첫 번째 매트 리퀴드 립&nbsp; - 타투 틴트</p>" +
       "<p>건조함 없이 가볍게 밀착되는 편안한 핏</p>" +
       "<p>고급스러운 매트 피니쉬의 독보적인 18가지 타투 컬러</p>" +
       "<p>오랫동안 흐트러짐 없는 강력한 지속력</p>" +
       "<p>작은 디테일이 만드는 차원이 다른 럭셔리</p>" +
       "<p>매트 립 = 건조하고 답답하다는 편견을 깬</p>" +
       "<p>입생로랑 타투 틴트를 경험해보세요</p>" +
       "<img alt=\"\" width=\"730\" height=\"3135\" src=\"http://cdn.galleria.co.kr/uploadFile/FCKeditor/Image/Vendoritem2/edenhill/sieg/tint.jpg\">" +
       "</div>" +
       "</div>";

    Document document = Jsoup.parse(html);
    Elements descriptions = document.getElementsByClass("js_goods_description");
    if (descriptions.size() > 0) {
      Element element = descriptions.get(0);
      Elements children = element.children();
      System.out.println("Children size: " + children.size());

      children.forEach(e -> {
        System.out.println(e.tag() + ", " + e.attr("style") + ", " + e.ownText() + ", " + e.attr("src"));
      });
    }
  }
}
