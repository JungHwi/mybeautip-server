package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
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
import com.jocoos.mybeautip.video.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/videos", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoController {
  private final MemberService memberService;
  private final GoodsService goodsService;
  private final VideoService videoService;
  private final MemberRepository memberRepository;
  private final GoodsRepository goodsRepository;
  private final VideoRepository videoRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final VideoCommentRepository videoCommentRepository;

  public VideoController(MemberService memberService,
                         VideoService videoService,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsService goodsService,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         VideoRepository videoRepository,
                         VideoCommentRepository videoCommentRepository) {
    this.memberService = memberService;
    this.videoService = videoService;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsService = goodsService;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoRepository = videoRepository;
    this.videoCommentRepository = videoCommentRepository;
  }

  @Transactional
  @PostMapping("/{video_key}")
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

    memberRepository.updateVideoCount(me, 1);

    for (String goodsNo : relatedGoods) {
      goodsRepository.findById(goodsNo)
        .map(goods -> videoGoodsRepository.save(new VideoGoods(video, goods)))
        .orElseThrow(() -> new NotFoundException("goods_not_found", "goods not found: " + goodsNo));
    }
  }

  @Transactional
  @DeleteMapping("/{video_key}")
  public void setVideoRelatedGoods(@PathVariable("video_key") Long videoKey) {
    videoGoodsRepository.deleteByVideoVideoKey(videoKey);
    videoRepository.deleteByVideoKey(videoKey);
    memberRepository.updateVideoCount(memberService.currentMemberId(), -1);
  }

  @GetMapping("/{video_key}/goods")
  public List<GoodsInfo> getRelatedGoods(@PathVariable("video_key") Long videoKey) {
    List<VideoGoods> list = videoGoodsRepository.findAllByVideoVideoKey(videoKey);

    List<GoodsInfo> relatedGoods = new ArrayList<>();
    for (VideoGoods video : list) {
      relatedGoods.add(goodsService.generateGoodsInfo(video.getGoods()));
    }
    return relatedGoods;
  }

  @GetMapping("/{videoKey}/comment_count")
  public CommentCountResponse getVideoCommentCount(@PathVariable Long videoKey) {
    return videoRepository.findByVideoKey(videoKey)
      .map(v -> new CommentCountResponse(v.getCommentCount()))
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, video key: " + videoKey));
  }

  @GetMapping("/{videoKey}/comments")
  public CursorResponse getVideoComments(@PathVariable Long videoKey,
                                         @RequestParam(defaultValue = "20") int count,
                                         @RequestParam(required = false) String cursor,
                                         @RequestParam(required = false) Long parentId) {
    PageRequest page = PageRequest.of(0, count);
    Slice<VideoComment> comments;

    if (parentId != null) {
      comments = videoService.findCommentsByParentId(parentId, cursor, page);
    } else {
      comments = videoService.findCommentsByVideoKey(videoKey, cursor, page);
    }

    List<VideoController.VideoCommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> result.add(
      memberRepository.findById(comment.getCreatedBy())
        .map(member -> new VideoCommentInfo(comment, new MemberInfo(member, memberService.getFollowingId(member))))
        .orElseGet(() -> new VideoCommentInfo(comment))
    ));

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse
      .Builder<>("/api/1/videos/" + videoKey + "/comments", result)
      .withCount(count)
      .withCursor(nextCursor).toBuild();
  }

  @Transactional
  @PostMapping("/{videoKey:.+}/comments")
  public ResponseEntity addVideoComment(@PathVariable Long videoKey,
                                        @RequestBody VideoController.CreateCommentRequest request,
                                        BindingResult bindingResult) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    videoRepository.findByVideoKey(videoKey)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, video key: " + videoKey));

    if (request.getParentId() != null) {
      videoCommentRepository.findById(request.getParentId())
        .map(parent -> {
          videoCommentRepository.updateCommentCount(parent.getId(), 1);
          return Optional.empty();
        })
        .orElseThrow(() -> new NotFoundException("comment_id_not_found", "invalid comment parent id"));
    }

    VideoComment videoComment = new VideoComment(videoKey);
    BeanUtils.copyProperties(request, videoComment);
    videoRepository.updateCommentCount(videoKey, 1);

    return new ResponseEntity<>(
      new VideoController.VideoCommentInfo(videoCommentRepository.save(videoComment)),
      HttpStatus.OK
    );
  }

  @PatchMapping("/{videoKey:.+}/comments/{id:.+}")
  public ResponseEntity updateVideoComment(@PathVariable Long videoKey,
                                           @PathVariable Long id,
                                           @RequestBody VideoController.UpdateCommentRequest request,
                                           BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long memberId = memberService.currentMemberId();
    return videoCommentRepository.findByIdAndVideoKeyAndCreatedBy(id, videoKey, memberId)
      .map(comment -> {
        comment.setComment(request.getComment());
        return new ResponseEntity<>(
          new VideoController.VideoCommentInfo(videoCommentRepository.save(comment)),
          HttpStatus.OK
        );
      })
      .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video key id or comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoKey:.+}/comments/{id:.+}")
  public ResponseEntity<?> removeVideoComment(@PathVariable Long videoKey,
                                              @PathVariable Long id) {
    videoRepository.updateCommentCount(videoKey, -1);

    Long memberId = memberService.currentMemberId();
    return videoCommentRepository.findByIdAndVideoKeyAndCreatedBy(id, videoKey, memberId)
      .map(comment -> {
        if (comment.getParentId() != null) {
          videoCommentRepository.updateCommentCount(comment.getParentId(), -1);
        }
        videoCommentRepository.delete(comment);
        return new ResponseEntity<>(HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video key or comment id"));
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
    private Integer commentCount;
    private MemberInfo member;
    private Date createdAt;

    public VideoInfo(Video video, MemberInfo member) {
      BeanUtils.copyProperties(video, this);
      this.member = member;
    }
  }

  @Data
  private static class CreateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;

    private Long parentId;
  }

  @Data
  private static class UpdateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;
  }

  @Data
  static class VideoCommentInfo {
    private Long id;
    private Long videoKey;
    private String comment;
    private Long parentId;
    private int commentCount;
    private Long createdBy;
    private Date createdAt;
    private MemberInfo owner;
    private String commentRef;

    VideoCommentInfo(VideoComment comment) {
      BeanUtils.copyProperties(comment, this);
      setCommentRef(comment);
    }

    VideoCommentInfo(VideoComment comment, MemberInfo member) {
      this(comment);
      this.owner = member;
      setCommentRef(comment);
    }

    private void setCommentRef(VideoComment comment) {
      if (comment != null && comment.getCommentCount() > 0) {
        this.commentRef = String.format("/api/1/videos/%d/comments?parentId=%d", comment.getVideoKey(), comment.getId());
      }
    }
  }

  @Data
  @AllArgsConstructor
  private class CommentCountResponse {
    private Integer commentCount;
  }
}