package com.jocoos.mybeautip.goods;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoodsRepository extends JpaRepository<Goods, String> {
  
  @Query("select g from Goods g where g.modifiedAt < :cursor order by g.modifiedAt desc")
  Slice<Goods> getGoodsList(@Param("cursor")Date cursor,
                            Pageable pageable);

  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
      "and g.modifiedAt < :cursor order by g.modifiedAt desc")
  Slice<Goods> findAllByCategory(@Param("category")String category,
                                 @Param("cursor")Date cursor,
                                 Pageable of);
  
  @Query("select g from Goods g where " +
      "(g.goodsNm like concat('%',:keyword,'%') " +
      "or g.goodsDescription like concat('%',:keyword,'%')) " +
      "and g.modifiedAt < :cursor order by g.modifiedAt desc")
  Slice<Goods> findAllByKeyword(@Param("keyword")String keyword,
                                @Param("cursor")Date cursor,
                                Pageable of);
  
  @Query("select g from Goods g where g.allCd like concat('%',:category,'%') " +
      "and (g.goodsNm like concat('%',:keyword,'%') " +
      "or g.goodsDescription like concat('%',:keyword,'%')) " +
      "and g.modifiedAt < :cursor order by g.modifiedAt desc")
  Slice<Goods> findAllByCategoryAndKeyword(@Param("category")String category,
                                           @Param("keyword")String keyword,
                                           @Param("cursor")Date cursor,
                                           Pageable of);

  @Query("select g from Goods g where g.scmNo = ?2 " +
    "and g.modifiedAt < ?1 order by g.modifiedAt desc")
  Slice<Goods> findByScmNo(Date createdAt, Integer scmNo, Pageable pageable);

  Optional<Goods> findByGoodsNo(String goodsNo);

  @Modifying
  @Query("update Goods g set g.likeCount = g.likeCount + ?2, g.modifiedAt = now() " +
      "where g.goodsNo = ?1")
  void updateLikeCount(String goodsNo, Integer count);

}