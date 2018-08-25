package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
  private final VideoLikeRepository videoLikeRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final VideoCommentRepository videoCommentRepository;
  private final VideoCommentLikeRepository videoCommentLikeRepository;

  public VideoController(MemberService memberService,
                         VideoService videoService,
                         VideoGoodsRepository videoGoodsRepository,
                         GoodsService goodsService,
                         MemberRepository memberRepository,
                         GoodsRepository goodsRepository,
                         VideoRepository videoRepository,
                         VideoCommentRepository videoCommentRepository,
                         VideoLikeRepository videoLikeRepository,
                         VideoCommentLikeRepository videoCommentLikeRepository) {
    this.memberService = memberService;
    this.videoService = videoService;
    this.videoGoodsRepository = videoGoodsRepository;
    this.goodsService = goodsService;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoRepository = videoRepository;
    this.videoCommentRepository = videoCommentRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.videoCommentLikeRepository = videoCommentLikeRepository;
  }

  @Transactional
  @PostMapping
  public CreateVideoResponse createVideo(@Valid @RequestBody CreateVideoGoodsRequest request,
                          BindingResult bindingResult) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    if (videoRepository.findByVideoKey(request.getVideoKey()).isPresent()) {
      throw new BadRequestException("Already exist, video key: " + request.getVideoKey());
    }

    List<String> relatedGoods = request.getRelatedGoods();
    if (relatedGoods.size() == 0 || relatedGoods.size() > 10) {
      throw new BadRequestException("related goods count is valid between 1 to 10.");
    }

    String url = goodsRepository.getOne(relatedGoods.get(0)).getListImageData().toString();
    Video video = videoRepository.save(new Video(request.getVideoKey(),
      memberRepository.getOne(memberId), relatedGoods.size(), url));

    memberRepository.updateVideoCount(memberId, 1);

    for (String goodsNo : relatedGoods) {
      goodsRepository.findById(goodsNo)
        .map(goods -> videoGoodsRepository.save(new VideoGoods(video, goods)))
        .orElseThrow(() -> new NotFoundException("goods_not_found", "goods not found: " + goodsNo));
    }

    return new CreateVideoResponse(video.getId());
  }

  @Transactional
  @DeleteMapping("/{id}")
  public void deleteVideo(@PathVariable("id") Long id) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    videoRepository.findByIdAndMemberIdAndDeletedAtIsNull(id, memberId).map(video -> {
      videoGoodsRepository.deleteByVideoId(id);
      video.setDeletedAt(new Date());
      videoRepository.save(video);
      memberRepository.updateVideoCount(memberService.currentMemberId(), -1);
      return Optional.empty();
    }).orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));
  }

  @GetMapping("/{id}/goods")
  public List<GoodsInfo> getRelatedGoods(@PathVariable("id") Long id) {
    List<VideoGoods> list = videoGoodsRepository.findAllByVideoId(id);

    List<GoodsInfo> relatedGoods = new ArrayList<>();
    for (VideoGoods video : list) {
      relatedGoods.add(goodsService.generateGoodsInfo(video.getGoods()));
    }
    return relatedGoods;
  }

  @GetMapping("/{id}/comments")
  public CursorResponse getVideoComments(@PathVariable Long id,
                                         @RequestParam(defaultValue = "20") int count,
                                         @RequestParam(required = false) String cursor,
                                         @RequestParam(required = false) Long parentId) {
    PageRequest page = PageRequest.of(0, count);
    Slice<VideoComment> comments;
    Long me = memberService.currentMemberId();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    if (parentId != null) {
      comments = videoService.findCommentsByParentId(parentId, cursor, page);
    } else {
      comments = videoService.findCommentsByVideoId(id, cursor, page);
    }

    List<VideoController.VideoCommentInfo> result = Lists.newArrayList();
    comments.stream().forEach(comment -> {
      VideoCommentInfo commentInfo = new VideoCommentInfo(comment, createMemberInfo(comment
          .getCreatedBy()));
      videoCommentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), me)
          .ifPresent(liked -> commentInfo.setLikedId(liked.getId()));
      result.add(commentInfo);
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse
      .Builder<>("/api/1/videos/" + id + "/comments", result)
      .withCount(count)
      .withCursor(nextCursor)
      .withTotalCount(videoRepository.getCommentCount(id)).toBuild();
  }

  @Transactional
  @PostMapping("/{id:.+}/comments")
  public ResponseEntity addVideoComment(@PathVariable Long id,
                                        @RequestBody VideoController.CreateCommentRequest request,
                                        BindingResult bindingResult) {
    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Member me = memberService.currentMember();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    videoRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, id: " + id));

    if (request.getParentId() != null) {
      videoCommentRepository.findById(request.getParentId())
        .map(parent -> {
          videoCommentRepository.updateCommentCount(parent.getId(), 1);
          return Optional.empty();
        })
        .orElseThrow(() -> new NotFoundException("comment_id_not_found", "invalid comment parent id"));
    }

    VideoComment videoComment = new VideoComment(id, me);
    BeanUtils.copyProperties(request, videoComment);
    videoRepository.updateCommentCount(id, 1);

    return new ResponseEntity<>(
      new VideoController.VideoCommentInfo(videoCommentRepository.save(videoComment)),
      HttpStatus.OK
    );
  }

  @PatchMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity updateVideoComment(@PathVariable Long videoId,
                                           @PathVariable Long id,
                                           @RequestBody VideoController.UpdateCommentRequest request,
                                           BindingResult bindingResult) {

    if (bindingResult != null && bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long memberId = memberService.currentMemberId();
    return videoCommentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, memberId)
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
  @DeleteMapping("/{videoId:.+}/comments/{id:.+}")
  public ResponseEntity<?> removeVideoComment(@PathVariable Long videoId,
                                              @PathVariable Long id) {
    videoRepository.updateCommentCount(videoId, -1);

    Long memberId = memberService.currentMemberId();
    return videoCommentRepository.findByIdAndVideoIdAndCreatedById(id, videoId, memberId)
      .map(comment -> {
        if (comment.getParentId() != null) {
          videoCommentRepository.updateCommentCount(comment.getParentId(), -1);
        }
        List<VideoCommentLike> commentLikes = videoCommentLikeRepository.findAllByCommentId(
            comment.getId());
        videoCommentLikeRepository.deleteAll(commentLikes);
        videoCommentRepository.delete(comment);
        return new ResponseEntity<>(HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video key or comment id"));
  }

  @Transactional
  @PostMapping("/{videoId:.+}/likes")
  public ResponseEntity<VideoLikeInfo> addVideoLike(@PathVariable Long videoId) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return videoRepository.findByIdAndDeletedAtIsNull(videoId)
      .map(video -> {
        if (videoLikeRepository.findByVideoIdAndCreatedBy(videoId, memberId).isPresent()) {
          throw new BadRequestException("duplicated_video_like", "Already video liked");
        }

        videoRepository.updateLikeCount(videoId, 1);
        video.setLikeCount(video.getLikeCount() + 1);
        VideoLike videoLike = videoLikeRepository.save(new VideoLike(video));
        VideoLikeInfo info = new VideoLikeInfo(videoLike, videoService.generateVideoInfo(video));
        return new ResponseEntity<>(info, HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", "invalid video id: " + videoId));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeVideoLike(@PathVariable Long videoId,
                                           @PathVariable Long likeId) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return videoLikeRepository.findByIdAndVideoIdAndCreatedBy(likeId, videoId, memberId)
      .map(video -> {
        Optional<VideoLike> liked = videoLikeRepository.findById(likeId);
        if (!liked.isPresent()) {
          throw new NotFoundException("like_not_found", "invalid video like id");
        }

        videoLikeRepository.delete(liked.get());
        videoRepository.updateLikeCount(videoId, -1);
        return new ResponseEntity(HttpStatus.OK);
      })
      .orElseThrow(() -> new NotFoundException("video_not_found", "invalid video id or like id"));
  }

  @Transactional
  @PostMapping("/{videoId:.+}/comments/{commentId:.+}/likes")
  public ResponseEntity<VideoCommentLikeInfo> addVideoCommentLike(@PathVariable Long videoId,
                                                                  @PathVariable Long commentId) {
    Member member = memberService.currentMember();
    if (member == null) {
      throw new MemberNotFoundException("Login required");
    }

    return videoCommentRepository.findByIdAndVideoId(commentId, videoId)
        .map(comment -> {
          if (videoCommentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), member.getId
              ()).isPresent()) {
            throw new BadRequestException("duplicated_video_comment_like", "Already video comment liked");
          }


          videoCommentRepository.updateLikeCount(comment.getId(), 1);
          VideoCommentLike commentLikeLike = videoCommentLikeRepository.save(new VideoCommentLike
              (comment, member));
          return new ResponseEntity<>(new VideoCommentLikeInfo(commentLikeLike), HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video or " +
            "comment id"));
  }

  @Transactional
  @DeleteMapping("/{videoId:.+}/comments/{commentId:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeVideoCommentLike(@PathVariable Long videoId,
                                                 @PathVariable Long commentId,
                                                 @PathVariable Long likeId){
    Member me = memberService.currentMember();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    VideoComment videoComment = videoCommentRepository.findByIdAndVideoId(commentId, videoId)
        .orElseThrow(() -> new NotFoundException("video_comment_not_found", "invalid video id or comment " +
            "id"));

    return videoCommentLikeRepository.findByIdAndCommentIdAndCreatedById(likeId, videoComment
        .getId(), me.getId())
        .map(liked -> {
          videoCommentLikeRepository.delete(liked);
          videoCommentRepository.updateLikeCount(liked.getComment().getId(), -1);

          return new ResponseEntity(HttpStatus.OK);
        })
        .orElseThrow(() -> new NotFoundException("video_comment_like_not_found", "invalid video " +
            "comment like id"));
  }

  @Data
  public static class CreateVideoGoodsRequest {
    @NotNull
    String videoKey;

    @NotNull
    List<String> relatedGoods;
  }

  @Data
  @NoArgsConstructor
  public static class VideoInfo {
    private Long id;
    private String videoKey;
    private Integer commentCount;
    private Integer relatedGoodsCount;
    private String relatedGoodsThumbnailUrl;
    private Integer likeCount;
    private Long likeId;
    private MemberInfo member;
    private Date createdAt;

    public VideoInfo(Video video, MemberInfo member, Long likeId) {
      BeanUtils.copyProperties(video, this);
      this.member = member;
      this.likeId = likeId;
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
    private Long videoId;
    private String comment;
    private Long parentId;
    private int commentCount;
    private MemberInfo createdBy;
    private Date createdAt;
    private String commentRef;
    private Long likedId;

    VideoCommentInfo(VideoComment comment) {
      BeanUtils.copyProperties(comment, this);
      setCommentRef(comment);
    }

    VideoCommentInfo(VideoComment comment, MemberInfo createdBy) {
      this(comment);
      this.createdBy = createdBy;
    }

    private void setCommentRef(VideoComment comment) {
      if (comment != null && comment.getCommentCount() > 0) {
        this.commentRef = String.format("/api/1/videos/%d/comments?parentId=%d", comment.getVideoId(), comment.getId());
      }
    }
  }

  private MemberInfo createMemberInfo(Member member) {
    return new MemberInfo(member, memberService.getFollowingId(member));
  }

  @Data
  @AllArgsConstructor
  private class CreateVideoResponse {
    private Long id;
  }

  @Data
  @AllArgsConstructor
  private class CommentCountResponse {
    private Integer commentCount;
  }

  @Data
  public static class VideoLikeInfo {
    private Long id;
    private Long createdBy;
    private Date createdAt;
    private VideoInfo video;

    VideoLikeInfo(VideoLike videoLike, VideoInfo video) {
      BeanUtils.copyProperties(videoLike, this);
      this.video = video;
    }
  }

  @Data
  public static class VideoCommentLikeInfo {
    private Long id;
    private MemberInfo createdBy;
    private Date createdAt;
    private VideoCommentInfo comment;

    public VideoCommentLikeInfo(VideoCommentLike commentLike) {
      BeanUtils.copyProperties(commentLike, this);
      comment = new VideoCommentInfo(commentLike.getComment());
    }
  }
}