package com.jocoos.mybeautip.restapi;

import com.google.common.base.Strings;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.store.StoreLike;
import com.jocoos.mybeautip.store.StoreLikeRepository;
import com.jocoos.mybeautip.store.StoreRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/stores", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreController {

  private final MemberService memberService;
  private final StoreRepository storeRepository;
  private final StoreLikeRepository storeLikeRepository;

  public StoreController(MemberService memberService,
                         StoreRepository storeRepository,
                         StoreLikeRepository storeLikeRepository) {
    this.memberService = memberService;
    this.storeRepository = storeRepository;
    this.storeLikeRepository = storeLikeRepository;
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<StoreController.StoreInfo> getStore(@PathVariable Long id) {
    long me = memberService.currentMemberId();
    Optional<Store> optional = storeRepository.findById(id);
    StoreInfo storeInfo;
    if (optional.isPresent()) {
      Store store = optional.get();

      // Set like ID
      Optional<StoreLike> optionalStoreLike = storeLikeRepository.findByStoreIdAndCreatedBy(store.getId(), me);
      Long likeId = optionalStoreLike.isPresent() ? optionalStoreLike.get().getId() : null;

      storeInfo = new StoreInfo(store, likeId);
      return new ResponseEntity<>(storeInfo, HttpStatus.OK);
    } else {
      throw new NotFoundException("store_not_found", "invalid store id");
    }
  }

  @Transactional
  @PostMapping("/{id:.+}/likes")
  public ResponseEntity<StoreController.StoreLikeInfo> addStoreLike(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }

    return storeRepository.findById(id)
            .map(store -> {
              Long storeId = store.getId();
              if (storeLikeRepository.findByStoreIdAndCreatedBy(storeId, memberId).isPresent()) {
                throw new BadRequestException("duplicated_store_like", "Already store liked");
              }

              storeRepository.updateLikeCount(id, 1);
              StoreLike storeLike = storeLikeRepository.save(new StoreLike(store));
              return new ResponseEntity<>(new StoreLikeInfo(storeLike), HttpStatus.OK);
            })
            .orElseThrow(() -> new NotFoundException("store_not_found", "invalid store id"));
  }

  @Transactional
  @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
  public ResponseEntity<?> removeStoreLike(@PathVariable Long id,
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
  public static class StoreInfo {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer likeCount;
    private Long likeId;

    public StoreInfo(Store store, Long likeId) {
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
    private StoreInfo storeInfo;

    public StoreLikeInfo(StoreLike storeLike) {
      BeanUtils.copyProperties(storeLike, this);
      storeInfo = new StoreInfo(storeLike.getStore(), storeLike.getId());
    }
  }
}