package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberController;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoGoodsController {
  private final MemberService memberService;
  private final VideoGoodsRepository videoGoodsRepository;
  private final GoodsRepository goodsRepository;

  public VideoGoodsController(MemberService memberService,
                              VideoGoodsRepository videoGoodsRepository,
                              GoodsRepository goodsRepository) {
    this.memberService = memberService;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsRepository = goodsRepository;
  }

  @PostMapping("/api/1/videos/{video_key}/goods")
  public void createVideo(@PathVariable("video_key") String videoKey,
                          @Valid @RequestBody CreateVideoGoodsRequest request,
                          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    List<String> relatedGoods = request.getRelatedGoods();
    if (relatedGoods.size() == 0) {
      throw new BadRequestException("more than one related goods_no needed");
    }

    for (String goodsNo : relatedGoods) {
      if (goodsNo.length() != 10) {
        throw new BadRequestException("invalid goods_no: " + goodsNo);
      }

      goodsRepository.findById(goodsNo)
              .orElseThrow(() -> new NotFoundException("goods_not_found",
                      "goods not found: " + goodsNo));
    }

    long me = memberService.currentMemberId();

    for (String goodsNo : relatedGoods) {
      log.debug("goodsNo: " + goodsNo);
      videoGoodsRepository.findByVideoKeyAndGoodsNo(videoKey, goodsNo)
              .orElseGet(() -> videoGoodsRepository.save(new VideoGoods(videoKey, goodsNo, me,
                      request.getType(), request.getThumbnailUrl())));
    }
  }

  @DeleteMapping("/api/1/videos/{video_key}")
  public void setVideoRelatedGoods(@PathVariable("video_key") String videoKey) {
    List<VideoGoods> videoGoodsList = videoGoodsRepository.findAllByVideoKey(videoKey);
    videoGoodsRepository.deleteAll(videoGoodsList);
  }

  @GetMapping("/api/1/videos/{video_key}/goods")
  public List<GoodsInfo> getRelatedGoods(@PathVariable("video_key") String videoKey) {
    List<VideoGoods> list = videoGoodsRepository.findAllByVideoKey(videoKey);
    if (list == null || list.size() == 0) {
      throw new NotFoundException("goods_not_found", "goods not found: " + videoKey);
    }

    List<GoodsInfo> relatedGoods = new ArrayList<>();
    for (VideoGoods video : list) {
      Optional<Goods> optional = goodsRepository.findById(video.getGoodsNo());
      optional.ifPresent(goods -> relatedGoods.add(new GoodsInfo(goods)));
    }
    return relatedGoods;
  }

  @Data
  public static class CreateVideoGoodsRequest {
    @NotNull
    String type;

    @NotNull
    @Size(max = 200)
    String thumbnailUrl;

    @NotNull
    List<String> relatedGoods;
  }

  @Data
  @NoArgsConstructor
  public static class VideoGoodsInfo {
    private String videoKey;
    private String type;
    private String thumbnailUrl;
    private MemberController.MemberInfo member;

    public VideoGoodsInfo(VideoGoods video) {
      this.videoKey = video.getVideoKey();
      this.type = video.getVideoType();
      this.thumbnailUrl = video.getThumbnailUrl();
    }
  }
}