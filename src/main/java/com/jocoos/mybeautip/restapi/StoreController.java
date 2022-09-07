package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.store.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/stores", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreController {

    private static final String STORE_NOT_FOUND = "store.not_found";
    private static final String ALREADY_LIKED = "like.already_liked";
    private final LegacyMemberService legacyMemberService;
    private final GoodsService goodsService;
    private final MessageService messageService;
    private final StoreService storeService;
    private final StoreRepository storeRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final GoodsRepository goodsRepository;

    public StoreController(LegacyMemberService legacyMemberService,
                           GoodsService goodsService,
                           MessageService messageService,
                           StoreService storeService,
                           StoreRepository storeRepository,
                           StoreLikeRepository storeLikeRepository,
                           GoodsRepository goodsRepository) {
        this.legacyMemberService = legacyMemberService;
        this.goodsService = goodsService;
        this.messageService = messageService;
        this.storeService = storeService;
        this.storeRepository = storeRepository;
        this.storeLikeRepository = storeLikeRepository;
        this.goodsRepository = goodsRepository;
    }

    @GetMapping("/{id:.+}")
    public ResponseEntity<StoreController.StoreInfo> getStore(@PathVariable Integer id,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Long memberId = legacyMemberService.currentMemberId();
        Optional<Store> optional = storeRepository.findById(id);
        StoreInfo storeInfo;
        Long likeId = null;
        if (optional.isPresent()) {
            Store store = optional.get();

            if (memberId != null) {
                // Set like ID
                Optional<StoreLike> optionalStoreLike = storeLikeRepository.findByStoreIdAndCreatedById(store.getId(), memberId);
                likeId = optionalStoreLike.map(StoreLike::getId).orElse(null);
            }

            storeInfo = new StoreInfo(store, likeId);
            return new ResponseEntity<>(storeInfo, HttpStatus.OK);
        } else {
            throw new NotFoundException(messageService.getMessage(STORE_NOT_FOUND, lang));
        }
    }

    @GetMapping("/{id:.+}/goods")
    public CursorResponse getStoreGoods(@PathVariable Integer id,
                                        @RequestParam(defaultValue = "50") int count,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(required = false) String cursor,
                                        HttpServletRequest httpServletRequest,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(STORE_NOT_FOUND, lang)));

        Date startCursor = (org.apache.logging.log4j.util.Strings.isBlank(cursor)) ?
                new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));


        PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Goods> slice;
        if (StringUtils.isEmpty(category)) {
            slice = goodsRepository.findByCreatedAtBeforeAndScmNoAndStateLessThanEqual(startCursor, id, Goods.GoodsState.NO_SALE.ordinal(), pageable);
        } else {
            slice = goodsRepository.findByCreatedAtBeforeAndScmNoAndCateCd(startCursor, id, category, pageable);
        }

        List<GoodsInfo> result = new ArrayList<>();
        if (slice != null && slice.hasContent()) {
            for (Goods goods : slice.getContent()) {
                result.add(goodsService.generateGoodsInfo(goods));
            }
        }

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
        }
        return new CursorResponse.Builder<>(httpServletRequest.getRequestURI(), result)
                .withCount(count)
                .withCursor(nextCursor)
                .toBuild();
    }

    @PostMapping("/{id:.+}/likes")
    public ResponseEntity<StoreController.StoreLikeInfo> addStoreLike(@PathVariable Integer id,
                                                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Long memberId = legacyMemberService.currentMemberId();
        return storeRepository.findById(id)
                .map(store -> {
                    Integer storeId = store.getId();
                    if (storeLikeRepository.findByStoreIdAndCreatedById(storeId, memberId).isPresent()) {
                        throw new BadRequestException(messageService.getMessage(ALREADY_LIKED, lang));
                    }
                    StoreLike storeLike = storeService.addLike(store);
                    return new ResponseEntity<>(new StoreLikeInfo(storeLike), HttpStatus.OK);
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(STORE_NOT_FOUND, lang)));
    }

    @DeleteMapping("/{id:.+}/likes/{likeId:.+}")
    public ResponseEntity<?> removeStoreLike(@PathVariable Integer id,
                                             @PathVariable Long likeId) {
        Long memberId = legacyMemberService.currentMemberId();
        storeLikeRepository.findByIdAndStoreIdAndCreatedById(likeId, id, memberId)
                .orElseThrow(() -> new NotFoundException("invalid store id or like id"));

        storeLikeRepository.findById(likeId)
                .map(like -> {
                    storeService.removeLike(like);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException("invalid store like id"));

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/cover/{id:.+}")
    public Store updateStoreCoverImageUrl(@PathVariable Integer id) {
        log.info("updateStoreCoverImageUrl called: " + id);
        return storeRepository.findById(id)
                .map(store -> {
                    String url = StringUtils.substringBefore(store.getImageUrl(), "?");
                    store.setImageUrl(String.format("%s?time=%s", url, System.currentTimeMillis()));
                    log.info("updateStoreCoverImageUrl changed: " + store.getImageUrl());
                    return storeRepository.save(store);
                })
                .orElseThrow(() -> new NotFoundException("store not found:" + id));
    }

    @PatchMapping("/thumbnail/{id:.+}")
    public Store updateStoreThumbnailImageUrl(@PathVariable Integer id) {
        log.info("updateStoreThumbnailImageUrl called: " + id);
        return storeRepository.findById(id)
                .map(store -> {
                    String url = StringUtils.substringBefore(store.getThumbnailUrl(), "?");
                    store.setThumbnailUrl(String.format("%s?time=%s", url, System.currentTimeMillis()));
                    log.info("updateStoreThumbnailImageUrl changed: " + store.getThumbnailUrl());
                    return storeRepository.save(store);
                })
                .orElseThrow(() -> new NotFoundException("store not found:" + id));
    }

    /**
     * @see com.jocoos.mybeautip.store.Store
     */
    @Data
    @NoArgsConstructor
    public static class StoreInfo {
        private Integer id;
        private String name;
        private String description;
        private String centerPhone;
        private String imageUrl;
        private String thumbnailUrl;
        private Integer likeCount;
        private Long likeId;

        public StoreInfo(Store store, Long likeId) {
            BeanUtils.copyProperties(store, this);
            this.description = StringUtils.isBlank(store.getDescription()) ? "" : store.getDescription();
            this.likeId = likeId;
        }
    }

    @Data
    public static class StoreLikeInfo {
        private Long id;
        @Deprecated
        private Long createdBy;
        private Date createdAt;
        private StoreInfo store;

        StoreLikeInfo(StoreLike storeLike) {
            BeanUtils.copyProperties(storeLike, this);
            store = new StoreInfo(storeLike.getStore(), storeLike.getId());
        }
    }

    @Data
    static class UpdateStoreRequest {
        @Size(max = 255)
        private String imageUrl;

        @Size(max = 255)
        private String thumbnailUrl;

        @Size(max = 255)
        private String refundUrl;

        @Size(max = 255)
        private String asUrl;
    }
}