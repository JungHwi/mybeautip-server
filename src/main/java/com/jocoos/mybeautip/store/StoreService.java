package com.jocoos.mybeautip.store;

import com.jocoos.mybeautip.goods.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StoreService {

    private final GoodsRepository goodsRepository;
    private final StoreRepository storeRepository;
    private final StoreLikeRepository storeLikeRepository;

    public StoreService(GoodsRepository goodsRepository,
                        StoreRepository storeRepository,
                        StoreLikeRepository storeLikeRepository) {
        this.goodsRepository = goodsRepository;
        this.storeRepository = storeRepository;
        this.storeLikeRepository = storeLikeRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public StoreLike addLike(Store store) {
        storeRepository.updateLikeCount(store.getId(), 1);
        store.setLikeCount(store.getLikeCount() + 1);
        return storeLikeRepository.save(new StoreLike(store));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeLike(StoreLike like) {
        storeLikeRepository.delete(like);
        if (like.getStore().getLikeCount() > 0) {
            storeRepository.updateLikeCount(like.getStore().getId(), -1);
        }
    }
}
