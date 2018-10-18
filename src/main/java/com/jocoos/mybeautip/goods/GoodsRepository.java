package com.jocoos.mybeautip.goods;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoodsRepository extends JpaRepository<Goods, String> {

  @Query("select g from Goods g where g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> getGoodsList(@Param("cursor")Date cursor, Pageable pageable);

//  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
//    "and g.createdAt < :cursor order by g.createdAt desc")
  @Query("select g from Goods g where g.cateCd = :category " +
    "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findAllByCategory(@Param("category")String category,
                                 @Param("cursor")Date cursor,
                                 Pageable of);

  @Query("select g from Goods g where " +
    "(g.goodsNm like concat('%',:keyword,'%') " +
    "or g.goodsDescription like concat('%',:keyword,'%')) " +
    "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findAllByKeyword(@Param("keyword")String keyword,
                                @Param("cursor")Date cursor,
                                Pageable of);

  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
    "and (g.goodsNm like concat('%',:keyword,'%') " +
    "or g.goodsDescription like concat('%',:keyword,'%')) " +
    "and g.createdAt < :cursor order by g.createdAt desc")
  Slice<Goods> findAllByCategoryAndKeyword(@Param("category")String category,
                                           @Param("keyword")String keyword,
                                           @Param("cursor")Date cursor,
                                           Pageable of);

  Slice<Goods> findByCreatedAtBeforeAndScmNo(Date createdAt, Integer scmNo, Pageable pageable);

  Slice<Goods> findByCreatedAtBeforeAndScmNoAndCateCd(Date createdAt, Integer scmNo, String code, Pageable pageable);

  Optional<Goods> findByGoodsNo(String goodsNo);

  @Modifying
  @Query("update Goods g set g.likeCount = g.likeCount + ?2, g.modifiedAt = now() " +
    "where g.goodsNo = ?1")
  void updateLikeCount(String goodsNo, Integer count);

  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
    "and g.goodsNo != :itself order by g.hitCnt desc")
  List<Goods> findRelatedGoods(@Param("category")String category,
                               @Param("itself")String itself,
                               Pageable pageable);
}