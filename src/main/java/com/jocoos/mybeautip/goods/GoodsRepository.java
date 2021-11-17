package com.jocoos.mybeautip.goods;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoodsRepository extends JpaRepository<Goods, String> {

  @Query("select g from Goods g where g.state <= :state and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> getGoodsList(@Param("cursor")Date cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.state <= :state and g.likeCount < :cursor order by g.likeCount desc")
  Slice<Goods> getGoodsListOrderByLikeCountDesc(@Param("cursor")Integer cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.state <= :state and g.orderCnt < :cursor order by g.orderCnt desc")
  Slice<Goods> getGoodsListOrderByOrderCountDesc(@Param("cursor")Integer cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.state <= :state and g.hitCnt < :cursor order by g.hitCnt desc")
  Slice<Goods> getGoodsListOrderByHitCntDesc(@Param("cursor")Integer cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.state <= :state and g.reviewCnt < :cursor order by g.reviewCnt desc")
  Slice<Goods> getGoodsListOrderByReviewCntDesc(@Param("cursor")Integer cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.state <= :state and g.goodsPrice < :cursor order by g.goodsPrice desc")
  Slice<Goods> getGoodsListOrderByGoodsPriceDesc(@Param("cursor")Integer cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.state <= :state and g.goodsPrice > :cursor order by g.goodsPrice asc")
  Slice<Goods> getGoodsListOrderByGoodsPriceAsc(@Param("cursor")Integer cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.state <= :state and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> getGoodsListOrderByCreatedAtDesc(@Param("cursor")Date cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') and g.state <= :state " +
      "and g.likeCount < :cursor order by g.likeCount desc")
  Slice<Goods> getGoodsListCategoryAndOrderByLikeCountDesc(
      @Param("category")String category, @Param("cursor")Integer cursor, @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') and g.state <= :state " +
      "and g.orderCnt < :cursor order by g.orderCnt desc")
  Slice<Goods> getGoodsListCategoryAndOrderByOrderCountDesc(
      @Param("category")String category, @Param("cursor")Integer cursor,  @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') and g.state <= :state " +
      "and g.hitCnt < :cursor order by g.hitCnt desc")
  Slice<Goods> getGoodsListCategoryAndOrderByHitCntDesc(
      @Param("category")String category, @Param("cursor")Integer cursor,  @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') and g.state <= :state " +
      "and g.reviewCnt < :cursor order by g.reviewCnt desc")
  Slice<Goods> getGoodsListCategoryAndOrderByReviewCntDesc(
      @Param("category")String category, @Param("cursor")Integer cursor,  @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') and g.state <= :state " +
      "and g.goodsPrice < :cursor order by g.goodsPrice desc")
  Slice<Goods> getGoodsListCategoryAndOrderByGoodsPriceDesc(
      @Param("category")String category, @Param("cursor")Integer cursor,  @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') and g.state <= :state " +
      "and g.goodsPrice > :cursor order by g.goodsPrice asc")
  Slice<Goods> getGoodsListCategoryAndOrderByGoodsPriceAsc(
      @Param("category")String category, @Param("cursor")Integer cursor,  @Param("state")Integer maxValidState, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') and g.state <= :state " +
      "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> getGoodsListCategoryAndOrderByCreatedAtDesc(
      @Param("category")String category, @Param("cursor")Date cursor,  @Param("state")Integer maxValidState, Pageable pageable);

  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
      "and g.state <= :state and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findAllByCategory(@Param("category")String category,
                                 @Param("cursor")Date cursor,
                                 @Param("state")Integer maxValidState,
                                 Pageable of);

  @Query("select g from Goods g where " +
    "(g.goodsNm like concat('%',:keyword,'%') " +
    "or g.goodsDescription like concat('%',:keyword,'%') " +
    "or g.goodsSearchWord like concat('%',:keyword,'%')) " +
    "and g.state <= :state " +
    "order by g.orderCnt desc")
  Slice<Goods> findAllByKeyword(@Param("keyword")String keyword,
                                @Param("state")Integer maxValidState,
                                Pageable of);

  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
    "and (g.goodsNm like concat('%',:keyword,'%') " +
    "or g.goodsDescription like concat('%',:keyword,'%') " +
    "or g.goodsSearchWord like concat('%',:keyword,'%')) " +
      "and g.state <=2 " +
    "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findAllByCategoryAndKeyword(@Param("category")String category,
                                           @Param("keyword")String keyword,
                                           @Param("cursor")Date cursor,
                                           Pageable of);

  Slice<Goods> findByCreatedAtBeforeAndScmNoAndStateLessThanEqual(Date createdAt, Integer scmNo, int state, Pageable pageable);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
      "and g.scmNo = :store " +
      "and g.state <=2 " +
      "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findByCreatedAtBeforeAndScmNoAndCateCd(@Param("cursor")Date cursor,
                                                      @Param("store")Integer store,
                                                      @Param("category")String category,
                                                      Pageable pageable);

  Optional<Goods> findByGoodsNo(String goodsNo);
  
  Optional<Goods> findByGoodsNoAndStateLessThanEqual(String goodsNo, int state);
  

  @Modifying
  @Query("update Goods g set g.likeCount = g.likeCount + ?2, g.modifiedAt = now() " +
    "where g.goodsNo = ?1")
  void updateLikeCount(String goodsNo, Integer count);

  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
    "and g.goodsNo != :itself and g.state <=2 order by g.hitCnt desc")
  List<Goods> findRelatedGoods(@Param("category")String category,
                               @Param("itself")String itself,
                               Pageable pageable);


  Page<Goods> findByState(int state, Pageable pageable);

  Page<Goods> findByStateAndCateCd(int state, String code, Pageable pageable);


  Page<Goods> findByOrderByGoodsNoDesc(Pageable pageable);

  Page<Goods> findByScmNoOrderByGoodsNoDesc(Integer scmNo, Pageable pageable);

  Page<Goods> findByOrderByHitCntDesc(Pageable pageable);

  Page<Goods> findByOrderByOrderCntDesc(Pageable pageable);

  Page<Goods> findByOrderByLikeCountDesc(Pageable pageable);

  Page<Goods> findByScmNoOrderByHitCntDesc(Integer scmNo, Pageable pageable);

  Page<Goods> findByScmNoOrderByOrderCntDesc(Integer scmNo, Pageable pageable);

  Page<Goods> findByScmNoOrderByLikeCountDesc(Integer scmNo, Pageable pageable);


  Page<Goods> findByCateCdOrderByGoodsNoDesc(String code, Pageable pageable);

  Page<Goods> findByCateCdOrderByHitCntDesc(String code, Pageable pageable);

  Page<Goods> findByCateCdOrderByOrderCntDesc(String code, Pageable pageable);

  Page<Goods> findByCateCdOrderByLikeCountDesc(String code, Pageable pageable);


  Page<Goods> findByScmNoAndCateCdOrderByGoodsNoDesc(Integer scmNo, String code, Pageable pageable);

  Page<Goods> findByScmNoAndCateCdOrderByHitCntDesc(Integer scmNo, String code, Pageable pageable);

  Page<Goods> findByScmNoAndCateCdOrderByOrderCntDesc(Integer scmNo, String code, Pageable pageable);

  Page<Goods> findByScmNoAndCateCdOrderByLikeCountDesc(Integer scmNo, String code, Pageable pageable);

  Page<Goods> findByGoodsNoIn(Collection<String> goodses, Pageable pageable);

  Page<Goods> findByGoodsNmContainingOrderByGoodsNoDesc(String goodsNm, Pageable pageable);

}