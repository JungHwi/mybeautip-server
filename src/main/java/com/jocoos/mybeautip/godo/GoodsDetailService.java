package com.jocoos.mybeautip.godo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsRepository;

@Slf4j
@Service
public class GoodsDetailService {

  private static final String IMAGE_ELEMENT_FORMAT = "<img src=\"%s\" />";
  private static final String VIDEO_ELEMENT_FORMAT = "<div><video preload=\"auto\" controls=\"true\" x-webkit-ariplay=\"allow\" webkit-playsinline=\"allow\" playsinline=\"\" src=\"%s\"></video></div>";
  private static final String TEXT_ELEMENT_FORMAT = "<p style=\"%s\">%s</p>";

  private final GoodsRepository goodsRepository;

  @Value("${godomall.goods-view-url}")
  private String goodsViewUrl;

  public GoodsDetailService(GoodsRepository goodsRepository) {
    this.goodsRepository = goodsRepository;
  }

  public String getGoodsDetail(String goodsNo, boolean includeVideo)  {
    return goodsRepository.findById(goodsNo)
       .map(goods -> {
          String goodsDescription = goods.getGoodsDescription();
          log.debug("goods description: {}", goodsDescription);

          Element root;
          if (!StringUtils.isBlank(goodsDescription) && !hasComplicatedStyle(goodsDescription)) {
            root = createDocumentFromString(goodsDescription);
          } else {
            root = createDocumentFromUri(goodsNo);
          }

          return getGoodsDetailPage(root, includeVideo);
        })
       .orElse("");
  }

  private boolean hasComplicatedStyle(String document) {
    return !StringUtils.isBlank(document) && document.contains("background-image: url(");
  }

  @Cacheable("goods_detail")
  private Element createDocumentFromUri(String goodsNo) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(goodsViewUrl);
    builder.queryParam("goodsNo", goodsNo);
    String uri = builder.toUriString();
    log.debug("connect uri: {}", uri);
    try {
      Document document = Jsoup.connect(uri).get();
      Elements descriptions = document.getElementsByClass("js_goods_description");
      if (descriptions.size() <= 0) {
        log.warn("Cann't read goods page");
        log.debug(document.html());
        return null;
      }

      return descriptions.get(0);
    } catch (HttpStatusException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
        log.error("not found page - " + uri, e);
      }
      throw new NotFoundException("goods_view_not_found", "goods view page not found - " + goodsNo);
    } catch (IOException e) {
      log.error("goods view page not found", e);
      throw new NotFoundException("goods_view_not_found", "goods view page not found - " + goodsNo);
    }
  }

  private Element createDocumentFromString(String goodsDescription) {
    return Jsoup.parse(goodsDescription).body();
  }

  private String getGoodsDetailPage(Element root, boolean includeVideo)  {
    Elements children = root.children();
    Elements elements = new Elements();

    children.forEach(element -> {
      if (element.hasClass("openblock")) {
        return;
      }

      String text = element.text();
      String style = element.attr("style");

      if (!StringUtils.isBlank(text)) {
        log.debug("tag: {}, style: {}, text: {}", element.tag(), style, element.text());
        elements.add(element);
      }

      element.select("[src]").forEach(src -> {
        log.debug("tag: {}, src: {}", src.tag().getName(), src.attr("src"));
        elements.add(src);
      });
    });

    return createResultDocument(elements, includeVideo);
  }

  private String createResultDocument(Elements elements, boolean includeVideo) {
    StringBuilder builder = new StringBuilder();
    builder.append("<html>");
    builder.append("<head><meta name=\"viewport\" content=\"user-scalable=yes, width=device-width\">");
    builder.append("<style type=\"text/css\">body{margin:0;font-size:12px;}img{display:block;width:100%;max-width:100%;height:auto;}.detail-text{font-size:12px}p{margin:0}video{width:100%}</style>");
    builder.append("</head>");
    builder.append("<body>");
    builder.append("<div style=\"padding:15px 20px;\">");

    elements.forEach(element -> {
      log.debug("tag: {}, style: {}, childNodeSize: {}", element.tag().getName(), element.attr("style"), element.childNodeSize());

      switch (element.tag().getName().toLowerCase()) {
        case "img": {
          builder.append(String.format(IMAGE_ELEMENT_FORMAT, element.attr("src").replaceAll("\\\\\"","")));
          break;
        }
        case "video": {
          String videoSrc = element.attr("src");
          log.debug("video src: {}", videoSrc);

          if (includeVideo && !StringUtils.isBlank(videoSrc)) {
            builder.append(String.format(VIDEO_ELEMENT_FORMAT, videoSrc));
          }
          break;
        }
        case "div": {
          String style = element.attr("style");

          if (!StringUtils.isBlank(style)) {
            builder.append("<div style=\"" + element.attr("style") + "\">");
          } else {
            builder.append("<div>");
          }

          if (element.childNodeSize() > 0 ) {
            builder.append(createChildren(element));
          } else {
            builder.append(element.text());
          }

          builder.append("</div>");
          break;
        }
        case "span" :
        case "p" : {
          if (element.childNodeSize() > 0) {
            builder.append(createChildren(element));
          } else {
            builder.append(String.format(TEXT_ELEMENT_FORMAT, element.attr("style"), element.text()));
          }
          break;
        }
        default: {
          log.warn("Unknown tag name: {}", element.tag().getName());
        }
      }
    });

    builder.append("</div></body></html>");
    return builder.toString();
  }

  public String createChildren(Element element) {
    StringBuilder builder = new StringBuilder();

    element.children().forEach(c -> {
      if (c.childNodeSize() > 0) {
        builder.append(createChildren(c));
      }

      String tag = c.tag().getName();
      switch (tag) {
        case "button":
        case "a": {
          log.debug("ignore tag: {}", tag);
          break;
        }
        case "span":
        case "p": {
          String ownText = c.ownText();
          log.debug("tag: {}, own text: {}", tag, ownText);
          if (StringUtils.isBlank(ownText)) {
            break;
          }
          String style = c.attr("style");

          if (!StringUtils.isBlank(style)) {
            builder.append(String.format("<%s style=\"%s\">%s</%s>", tag, style, ownText, tag));
          } else {
            builder.append(String.format("<%s>%s</%s>", tag, ownText, tag));
          }
          break;
        }
        default:
          log.debug("Unknown tag: {}, {}", tag, c.text());
      }
    });

    return builder.toString();
  }
}
