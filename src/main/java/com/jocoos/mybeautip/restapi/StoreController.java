package com.jocoos.mybeautip.restapi;

import com.google.common.base.Strings;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.store.StoreLike;
import com.jocoos.mybeautip.store.StoreLikeRepository;
import com.jocoos.mybeautip.store.StoreRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/stores", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreController {

  private final MemberService memberService;
  private final GoodsService goodsService;
  private final StoreRepository storeRepository;
  private final StoreLikeRepository storeLikeRepository;
  private final GoodsRepository goodsRepository;

  public StoreController(MemberService memberService,
                         GoodsService goodsService,
                         StoreRepository storeRepository,
                         StoreLikeRepository storeLikeRepository,
                         GoodsRepository goodsRepository) {
    this.memberService = memberService;
    this.goodsService = goodsService;
    this.storeRepository = storeRepository;
    this.storeLikeRepository = storeLikeRepository;
    this.goodsRepository = goodsRepository;
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<StoreController.StoreInfo> getStore(@PathVariable Integer id) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }
    Optional<Store> optional = storeRepository.findById(id);
    StoreInfo storeInfo;
    if (optional.isPresent()) {
      Store store = optional.get();

      // Set like ID
      Optional<StoreLike> optionalStoreLike = storeLikeRepository.findByStoreIdAndCreatedBy(store.getId(), memberId);
      Long likeId = optionalStoreLike.map(StoreLike::getId).orElse(null);

      storeInfo = new StoreInfo(store, likeId);
      return new ResponseEntity<>(storeInfo, HttpStatus.OK);
    } else {
      throw new NotFoundException("store_not_found", "invalid store id");
    }
  }

  @GetMapping("/{id:.+}/goods")
  public CursorResponse getStoreGoods(@PathVariable Integer id,
                                      @RequestParam(defaultValue = "50") int count,
                                      @RequestParam(required = false) String cursor,
                                      HttpServletRequest httpServletRequest) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    storeRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("store_not_found", "invalid store id"));

    Date startCursor = (org.apache.logging.log4j.util.Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Goods> slice = goodsRepository.findByScmNo(startCursor, id, pageable);

    List<GoodsInfo> result = new ArrayList<>();
    if (slice != null && slice.hasContent()) {
      for (Goods goods : slice.getContent()) {
        result.add(goodsService.generateGoodsInfo(goods));
      }
    }

    if (result.size() > 0) {
      String nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
      return new CursorResponse.Builder<>(httpServletRequest.getRequestURI(), result)
        .withCount(count)
        .withCursor(nextCursor)
        .toBuild();
    } else {
      return new CursorResponse.Builder<>(httpServletRequest.getRequestURI(), result).toBuild();
    }
  }

  @Transactional
  @PostMapping("/{id:.+}/likes")
  public ResponseEntity<StoreController.StoreLikeInfo> addStoreLike(@PathVariable Integer id) {
    Long memberId = memberService.currentMemberId();
    return storeRepository.findById(id)
            .map(store -> {
              Integer storeId = store.getId();
              if (storeLikeRepository.findByStoreIdAndCreatedBy(storeId, memberId).isPresent()) {
                throw new BadRequestException("duplicated_store_like", "Already store liked");
              }

              storeRepository.updateLikeCount(id, 1);
              store.setLikeCount(store.getLikeCount() + 1);
              StoreLike storeLike = storeLikeRepository.save(new StoreLike(store));
              return new ResponseEntity<>(new StoreLikeInfo(storeLike), HttpStatus.OK);
            })
            .orElseThrow(() -> new NotFoundException("store_not_found", "invalid store id"));
  }

  @Transactional
  @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeStoreLike(@PathVariable Integer id,
                                           @PathVariable Long likeId){
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return storeLikeRepository.findByIdAndStoreIdAndCreatedBy(likeId, id, memberId)
            .map(store -> {
              Optional<StoreLike> liked = storeLikeRepository.findById(likeId);
              if (!liked.isPresent()) {
                throw new NotFoundException("like_not_found", "invalid store like id");
              }

              storeLikeRepository.delete(liked.get());
              storeRepository.updateLikeCount(id, -1);
              return new ResponseEntity(HttpStatus.OK);
            })
            .orElseThrow(() -> new NotFoundException("store_not_found", "invalid store id or like id"));
  }

  /**
   * @see com.jocoos.mybeautip.store.Store
   */
  @Data
  @NoArgsConstructor
  static class StoreInfo {
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer likeCount;
    private Long likeId;

    StoreInfo(Store store, Long likeId) {
      this.id = store.getId();
      this.name = store.getName();
      this.description = Strings.isNullOrEmpty(store.getDescription()) ? "" : store.getDescription();
      this.imageUrl = Strings.isNullOrEmpty(store.getImageUrl()) ? "" : store.getImageUrl();
      this.thumbnailUrl = Strings.isNullOrEmpty(store.getThumbnailUrl()) ? "" : store.getThumbnailUrl();
      this.likeCount = store.getLikeCount();
      this.likeId = likeId;
    }
  }

  @Data
  public static class StoreLikeInfo {
    private Long id;
    private Long createdBy;
    private Date createdAt;
    private StoreInfo store;

    StoreLikeInfo(StoreLike storeLike) {
      BeanUtils.copyProperties(storeLike, this);
      store = new StoreInfo(storeLike.getStore(), storeLike.getId());
    }
  }
}