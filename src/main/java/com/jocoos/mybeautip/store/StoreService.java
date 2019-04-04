package com.jocoos.mybeautip.store;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.goods.GoodsRepository;

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
  
  @Transactional
  public StoreLike addLike(Store store) {
    storeRepository.updateLikeCount(store.getId(), 1);
    store.setLikeCount(store.getLikeCount() + 1);
    return storeLikeRepository.save(new StoreLike(store));
  }
  
  @Transactional
  public void removeLike(StoreLike like) {
    storeLikeRepository.delete(like);
    storeRepository.updateLikeCount(like.getStore().getId(), -1);
  }
}
