package com.jocoos.mybeautip.goods;

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

  @Query("select g from Goods g " +
      "where g.state <=2 " +
      "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> getGoodsList(@Param("cursor")Date cursor, Pageable pageable);

  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
      "and g.state <=2 " +
      "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findAllByCategory(@Param("category")String category,
                                 @Param("cursor")Date cursor,
                                 Pageable of);

  @Query("select g from Goods g where " +
    "(g.goodsNm like concat('%',:keyword,'%') " +
    "or g.goodsDescription like concat('%',:keyword,'%') " +
    "or g.goodsSearchWord like concat('%',:keyword,'%')) " +
    "and g.state <=2 " +
    "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findAllByKeyword(@Param("keyword")String keyword,
                                @Param("cursor")Date cursor,
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

  Slice<Goods> findByCreatedAtBeforeAndScmNoAndCateCdAndStateLessThanEqual(Date createdAt, Integer scmNo, String code, int state, Pageable pageable);

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


  Page<Goods> findByScmNo(Integer scmNo, Pageable pageable);

  Page<Goods> findByScmNoAndCateCd(Integer scmNo, String code, Pageable pageable);

  Page<Goods> findByOrderByHitCntDesc(Pageable pageable);

  Page<Goods> findByOrderByOrderCntDesc(Pageable pageable);

  Page<Goods> findByOrderByLikeCountDesc(Pageable pageable);

  Page<Goods> findByScmNoOrderByHitCntDesc(Integer scmNo, Pageable pageable);

  Page<Goods> findByScmNoOrderByOrderCntDesc(Integer scmNo, Pageable pageable);

  Page<Goods> findByScmNoOrderByLikeCountDesc(Integer scmNo, Pageable pageable);

}