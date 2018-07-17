package com.jocoos.mybeautip.goods;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface GoodsRepository extends CrudRepository<Goods, String> {
  
  @Query("select g from Goods g where g.modifiedAt < :cursor order by g.modifiedAt desc")
  Slice<Goods> getGoodsList(@Param("cursor")Date cursor,
                            Pageable pageable);
  
  @Query("select g from Goods g where g.cateCd = :category " +
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
  
  @Query("select g from Goods g where g.cateCd = :category " +
      "and (g.goodsNm like concat('%',:keyword,'%') " +
      "or g.goodsDescription like concat('%',:keyword,'%')) " +
      "and g.modifiedAt < :cursor order by g.modifiedAt desc")
  Slice<Goods> findAllByCategoryAndKeyword(@Param("category")String category,
                                           @Param("keyword")String keyword,
                                           @Param("cursor")Date cursor,
                                           Pageable of);
}