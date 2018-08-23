package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsLike;
import com.jocoos.mybeautip.goods.GoodsLikeRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.word.BannedWordService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.post.PostLike;
import com.jocoos.mybeautip.post.PostLikeRepository;
import com.jocoos.mybeautip.store.StoreLike;
import com.jocoos.mybeautip.store.StoreLikeRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final MemberRepository memberRepository;
  private final PostLikeRepository postLikeRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final StoreLikeRepository storeLikeRepository;
  private final BannedWordService bannedWordService;

  @Value("${mybeautip.store.image-path.prefix}")
  private String storeImagePrefix;

  @Value("${mybeautip.store.image-path.cover-suffix}")
  private String storeImageSuffix;

  @Value("${mybeautip.store.image-path.thumbnail-suffix}")
  private String storeImageThumbnailSuffix;

  public MemberController(MemberService memberService,
                          GoodsService goodsService,
                          MemberRepository memberRepository,
                          PostLikeRepository postLikeRepository,
                          GoodsLikeRepository goodsLikeRepository,
                          StoreLikeRepository storeLikeRepository,
                          BannedWordService bannedWordService) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.memberRepository = memberRepository;
    this.postLikeRepository = postLikeRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.storeLikeRepository = storeLikeRepository;
    this.bannedWordService = bannedWordService;
  }

  @GetMapping("/me")
  public Resource<MemberInfo> getMe(Principal principal) {
    log.debug("member id: {}", principal.getName());
    return memberRepository.findById(Long.parseLong(principal.getName()))
              .map(m -> new Resource<>(new MemberInfo(m, null)))
              .orElseThrow(() -> new MemberNotFoundException("member_not_found"));
  }

  @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MemberInfo> updateMember(Principal principal,
                                             @Valid @RequestBody UpdateMemberRequest updateMemberRequest,
                                             BindingResult bindingResult) {

    log.debug("member id: {}", principal.getName());
    log.debug("binding result: {}", bindingResult);

    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid member request");
    }

    // Validation check: username cannot be empty
    if ("".equals(updateMemberRequest.getUsername())) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid member request - username");
    }

    // Validation check: email cannot be empty
    if ("".equals(updateMemberRequest.getEmail())) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid member request - email");
    }

    return memberRepository.findById(Long.parseLong(principal.getName()))
        .map(m -> {
          if (updateMemberRequest.getUsername() != null) {
            m.setUsername(validateUsername(updateMemberRequest.getUsername()));
          }
          if (updateMemberRequest.getEmail() != null) {
            m.setEmail(updateMemberRequest.getEmail());
          }
          if (updateMemberRequest.getAvatarUrl() != null) {
            m.setAvatarUrl(updateMemberRequest.getAvatarUrl());
          }
          if (updateMemberRequest.getIntro() != null) {
            m.setIntro(updateMemberRequest.getIntro());
          }
          memberRepository.save(m);

          return new ResponseEntity<>(new MemberInfo(m, null), HttpStatus.OK);
        })
        .orElseThrow(() -> new MemberNotFoundException(principal.getName()));
  }

  private String validateUsername(String username) {
    if (memberRepository.countByUsernameAndDeletedAtIsNull(username) > 0) {
      throw new BadRequestException("duplicated_username", "Your username is already in use");
    }

    return bannedWordService.findWordAndThrowException(username);
  }

  @GetMapping
  @ResponseBody
  public CursorResponse getMembers(@RequestParam(defaultValue = "20") int count,
                                               @RequestParam(required = false) String cursor,
                                               @RequestParam(required = false) String keyword) {
    Slice<Member> list = findMembers(keyword, cursor, count);
    List<MemberInfo> members = Lists.newArrayList();
    list.stream().forEach(m -> members.add(new MemberInfo(m, memberService.getFollowingId(m.getId()))));

    String nextCursor = null;
    if (members != null && members.size() > 0) {
      nextCursor = String.valueOf(members.get(members.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members", members)
       .withKeyword(keyword)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  @AllArgsConstructor
  @Data
  class Cursor<T> {
    private String keyword;
    private T cursor;
    private int count = 20;

    public boolean hasKeyword() {
      return !Strings.isNullOrEmpty(keyword);
    }

    public boolean hasCursor() {
      return cursor != null;
    }
  }

  // TODO: Refactor find member to MemberService
  private Slice<Member> findMembers(String keyword, String cursor, int count) {
    Cursor<Date> dateCursor = null;

    if (Strings.isNullOrEmpty(cursor)) {
      dateCursor = new Cursor<Date>(keyword, null, count);
    } else {
      Date toDate = null;
      try {
        toDate = new Date(Long.parseLong(cursor));
        dateCursor = new Cursor<Date>(keyword, toDate, count);
      } catch (NumberFormatException e) {
        log.error("Cannot convert cursor to Date", e);
        throw new BadRequestException("invalid cursor type");
      }
    }

    return findMembers(dateCursor);
  }

  private Slice<Member> findMembers(Cursor<Date> cursor) {
    Slice<Member> list = null;
    log.debug("Cursor: {}", cursor);

    PageRequest page = PageRequest.of(0, cursor.getCount(), new Sort(Sort.Direction.DESC, "createdAt"));

    if (cursor.hasCursor() && cursor.hasKeyword()) {
      list = memberRepository.findByUsernameContainingOrIntroContainingAndCreatedAtBeforeAndDeletedAtIsNull(cursor.getKeyword(), cursor.getKeyword(), cursor.getCursor(), page);
    } else if (cursor.hasCursor()) {
      list = memberRepository.findByCreatedAtBeforeAndDeletedAtIsNull(cursor.getCursor(), page);
    } else if (cursor.hasKeyword()) {
      list = memberRepository.findByUsernameContainingOrIntroContainingAndDeletedAtIsNull(cursor.getKeyword(), cursor.getKeyword(), page);
    } else {
      list = memberRepository.findByDeletedAtIsNull(page);
    }

    return list;
  }

  @GetMapping("/{id:.+}")
  public MemberInfo getMember(@PathVariable Long id) {
    return memberRepository.findById(id).map(m -> new MemberInfo(m, memberService.getFollowingId(m)))
        .orElseThrow(() -> new MemberNotFoundException(id));
  }

  @GetMapping(value = "/me/like_count")
  public LikeCountResponse getMyLikesCount() {
    LikeCountResponse response = new LikeCountResponse();
    response.setGoods(goodsLikeRepository.countByCreatedBy(memberService.currentMemberId()));
    response.setStore(storeLikeRepository.countByCreatedBy(memberService.currentMemberId()));
    response.setPost(postLikeRepository.countByCreatedBy(memberService.currentMemberId()));
    return response;
  }

  @GetMapping(value = "/{id:.+}/like_count")
  public LikeCountResponse getMemberLikesCount(@PathVariable Long id) {
    LikeCountResponse response = new LikeCountResponse();
    response.setGoods(goodsLikeRepository.countByCreatedBy(id));
    response.setStore(storeLikeRepository.countByCreatedBy(id));
    response.setPost(postLikeRepository.countByCreatedBy(id));
    return response;
  }

  @GetMapping(value = "/me/likes")
  public CursorResponse getMyLikes(@RequestParam String category,
                                   @RequestParam(defaultValue = "20") int count,
                                   @RequestParam(required = false) Long cursor) {

    return createLikeResponse(memberService.currentMemberId(), category, count, cursor, "/api/1/members/me/likes");
  }

  @GetMapping(value = "/{id:.+}/likes")
  public CursorResponse getMemberLikes(@PathVariable Long id,
                                       @RequestParam String category,
                                       @RequestParam(defaultValue = "20") int count,
                                       @RequestParam(required = false) Long cursor) {

    log.debug("id:{}", id);

    return createLikeResponse(id, category, count, cursor, String.format("/api/1/members/%d/likes", id));
  }

  private CursorResponse createLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    switch (category) {
      case "post":
        return createPostLikeResponse(memberId, category, count, cursor, uri);
      case "goods":
        return createGoodsLikeResponse(memberId, category, count, cursor, uri);
      case "store":
        return createStoreLikeResponse(memberId, category, count, cursor, uri);
      default:
        throw new BadRequestException("invalid category: " + category);
    }
  }

  private CursorResponse createPostLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<PostLike> postLikes = null;
    List<PostController.PostLikeInfo> result = Lists.newArrayList();

    if (cursor != null) {
      postLikes = postLikeRepository.findByCreatedAtBeforeAndCreatedBy(new Date(cursor), memberId, pageable);
    } else {
      postLikes = postLikeRepository.findByCreatedBy(memberId, pageable);
    }

    postLikes.stream().forEach(like -> result.add(new PostController.PostLikeInfo(like)));

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<PostController.PostLikeInfo>(uri, result)
       .withCategory(category)
       .withCursor(nextCursor)
       .withCount(count).toBuild();
  }

  private CursorResponse createGoodsLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<GoodsLike> goodsLikes;
    List<GoodsInfo> result = Lists.newArrayList();

    if (cursor != null) {
      goodsLikes = goodsLikeRepository.findByCreatedAtBeforeAndCreatedBy(
        new Date(cursor), memberId, pageable);
    } else {
      goodsLikes = goodsLikeRepository.findByCreatedBy(memberId, pageable);
    }

    Long likeId;
    Long me = memberService.currentMemberId();
    for (GoodsLike like : goodsLikes) {
      result.add(goodsService.generateGoodsInfo(like.getGoods()));
    }

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(goodsLikes.getContent().get(goodsLikes.getContent().size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>(uri, result)
      .withCategory(category)
      .withCursor(nextCursor)
      .withCount(count).toBuild();
  }

  private CursorResponse createStoreLikeResponse(Long memberId, String category, int count, Long cursor, String uri) {
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<StoreLike> storeLikes;
    List<StoreController.StoreInfo> result = Lists.newArrayList();

    if (cursor != null) {
      storeLikes = storeLikeRepository.findByCreatedAtBeforeAndCreatedBy(new Date(cursor), memberId, pageable);
    } else {
      storeLikes = storeLikeRepository.findByCreatedBy(memberId, pageable);
    }

    Long likeId;
    Long me = memberService.currentMemberId();
    for (StoreLike like : storeLikes) {
      likeId = me.equals(like.getCreatedBy())? like.getId() : null;
      String imageUrl = String.format("%s%d%s", storeImagePrefix, like.getStore().getId(), storeImageSuffix);
      String thumbnailUrl = String.format("%s%d%s", storeImagePrefix, like.getStore().getId(), storeImageThumbnailSuffix);
      result.add(new StoreController.StoreInfo(like.getStore(), likeId, imageUrl, thumbnailUrl));
    }

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(storeLikes.getContent().get(storeLikes.getContent().size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>(uri, result)
      .withCategory(category)
      .withCursor(nextCursor)
      .withCount(count).toBuild();
  }

  @NoArgsConstructor
  @Data
  public static class UpdateMemberRequest {

    @Size(max = 50)
    private String username;

    @Size(max = 50)
    private String email;

    @Size(max = 200)
    private String avatarUrl;

    @Size(max = 200)
    private String intro;
  }

  @Data
  private static class LikeCountResponse {
    private Integer goods = 0;
    private Integer store = 0;
    private Integer post = 0;
  }
}
