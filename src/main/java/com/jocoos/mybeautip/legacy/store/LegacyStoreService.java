package com.jocoos.mybeautip.legacy.store;

import com.jocoos.mybeautip.goods.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LegacyStoreService {

    private final GoodsRepository goodsRepository;
    private final LegacyStoreRepository legacyStoreRepository;
    private final LegacyStoreLikeRepository legacyStoreLikeRepository;

    public LegacyStoreService(GoodsRepository goodsRepository,
                              LegacyStoreRepository legacyStoreRepository,
                              LegacyStoreLikeRepository legacyStoreLikeRepository) {
        this.goodsRepository = goodsRepository;
        this.legacyStoreRepository = legacyStoreRepository;
        this.legacyStoreLikeRepository = legacyStoreLikeRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LegacyStoreLike addLike(LegacyStore legacyStore) {
        legacyStoreRepository.updateLikeCount(legacyStore.getId(), 1);
        legacyStore.setLikeCount(legacyStore.getLikeCount() + 1);
        return legacyStoreLikeRepository.save(new LegacyStoreLike(legacyStore));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeLike(LegacyStoreLike like) {
        legacyStoreLikeRepository.delete(like);
        if (like.getLegacyStore().getLikeCount() > 0) {
            legacyStoreRepository.updateLikeCount(like.getLegacyStore().getId(), -1);
        }
    }
}
