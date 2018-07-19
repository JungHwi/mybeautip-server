package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberController;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoController {
  private final MemberService memberService;
  private final VideoRepository videoRepository;
  private final GoodsRepository goodsRepository;

  public VideoController(MemberService memberService,
                         VideoRepository videoRepository,
                         GoodsRepository goodsRepository) {
    this.memberService = memberService;
    this.videoRepository = videoRepository;
    this.goodsRepository = goodsRepository;
  }

  @PostMapping("/api/1/videos")
  @Transactional
  public void createVideo(@Valid @RequestBody CreateVideoRequest request,
                          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid videos request");
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
    String videoKey = request.getVideoKey();

    for (String goodsNo : relatedGoods) {
      log.debug("goodsNo: " + goodsNo);
      videoRepository.findByVideoKeyAndGoodsNo(videoKey, goodsNo)
              .orElseGet(() -> videoRepository.save(new Video(videoKey, goodsNo, me,
                      request.getType(), request.getThumbnailUrl())));
    }
  }

  @DeleteMapping("/api/1/videos/{video_key}")
  public void setVideoRelatedGoods(@PathVariable("video_key") String videoKey) {
    if (videoKey.length() < 4 || videoKey.length() > 100) {
      throw new BadRequestException("invalid video_key: " + videoKey);
    }

    List<Video> Videos = videoRepository.findAllByVideoKey(videoKey);
    videoRepository.deleteAll(Videos);
  }

  @GetMapping("/api/1/videos/{video_key}/goods")
  public List<GoodsInfo> getRelatedGoods(@PathVariable("video_key") String videoKey) {
    if (videoKey.length() < 4 || videoKey.length() > 100) {
      throw new BadRequestException("invalid video_key: " + videoKey);
    }

    List<Video> list = videoRepository.findAllByVideoKey(videoKey);
    if (list == null) {
      throw new NotFoundException("goods_not_found", "goods not found: " + videoKey);
    }

    List<GoodsInfo> relatedGoods = new ArrayList<>();
    for (Video video : list) {
      Optional<Goods> optional = goodsRepository.findById(video.getGoodsNo());
      optional.ifPresent(goods -> relatedGoods.add(new GoodsInfo(goods)));
    }
    return relatedGoods;
  }

  @Data
  public class CreateVideoRequest {
    @NotNull
    @Size(min = 4, max = 100)
    String videoKey;

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
  public static class VideoInfo {
    private String videoKey;
    private String type;
    private String thumbnailUrl;
    private MemberController.MemberInfo member;

    public VideoInfo(Video video) {
      this.videoKey = video.getVideoKey();
      this.type = video.getVideoType();
      this.thumbnailUrl = video.getThumbnailUrl();
    }
  }
}