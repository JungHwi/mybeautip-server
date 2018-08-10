package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoController {
  private final MemberService memberService;
  private final GoodsService goodsService;
  private final MemberRepository memberRepository;
  private final GoodsRepository goodsRepository;
  private final VideoRepository videoRepository;
  private final VideoGoodsRepository videoGoodsRepository;

  public VideoController(MemberService memberService,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsService goodsService,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         VideoRepository videoRepository) {
    this.memberService = memberService;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsService = goodsService;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoRepository = videoRepository;
  }

  @Transactional
  @PostMapping("/api/1/videos/{video_key}")
  public void createVideo(@PathVariable("video_key") Long videoKey,
                          @Valid @RequestBody CreateVideoGoodsRequest request,
                          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    if (videoRepository.findByVideoKey(videoKey).isPresent()) {
      throw new BadRequestException("Already exist, video key: " + videoKey);
    }

    Long me = memberService.currentMemberId();

    List<String> relatedGoods = request.getRelatedGoods();
    if (relatedGoods.size() == 0 || relatedGoods.size() > 10) {
      throw new BadRequestException("related goods count is valid between 1 to 10.");
    }

    Video video = videoRepository.save(new Video(videoKey, request.getType(),
      request.getThumbnailUrl(), memberRepository.getOne(me)));

    for (String goodsNo : relatedGoods) {
      goodsRepository.findById(goodsNo)
        .map(goods -> videoGoodsRepository.save(new VideoGoods(video, goods)))
        .orElseThrow(() -> new NotFoundException("goods_not_found", "goods not found: " + goodsNo));
    }
  }

  @Transactional
  @DeleteMapping("/api/1/videos/{video_key}")
  public void setVideoRelatedGoods(@PathVariable("video_key") Long videoKey) {
    videoGoodsRepository.deleteByVideoVideoKey(videoKey);

    videoRepository.deleteByVideoKey(videoKey);
  }

  @GetMapping("/api/1/videos/{video_key}/goods")
  public List<GoodsInfo> getRelatedGoods(@PathVariable("video_key") Long videoKey) {
    List<VideoGoods> list = videoGoodsRepository.findAllByVideoVideoKey(videoKey);

    List<GoodsInfo> relatedGoods = new ArrayList<>();
    for (VideoGoods video : list) {
      relatedGoods.add(goodsService.generateGoodsInfo(video.getGoods()));
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
  public static class VideoInfo {
    private Long videoKey;
    private String type;
    private String thumbnailUrl;
    private MemberInfo member;
    private Date createdAt;

    public VideoInfo(Video video, MemberInfo member) {
      this.videoKey = video.getVideoKey();
      this.type = video.getType();
      this.thumbnailUrl = video.getThumbnailUrl();
      this.member = member; // FIXME:
      this.createdAt = video.getCreatedAt();
    }
  }
}