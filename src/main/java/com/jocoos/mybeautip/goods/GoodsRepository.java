package com.jocoos.mybeautip.goods;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GoodsRepository extends CrudRepository<Goods, String> {
  
  @Query("select g from Goods g where g.updatedAt < :cursor order by g.updatedAt desc")
  Slice<Goods> getGoodsList(@Param("cursor")Long cursor,
                            Pageable pageable);
  
  @Query("select g from Goods g where g.cateCd = :category " +
      "and g.updatedAt < :cursor order by g.updatedAt desc")
  Slice<Goods> findAllByCategory(@Param("category")String category,
                                 @Param("cursor")Long cursor,
                                 Pageable of);
  
  @Query("select g from Goods g where " +
      "(g.goodsNm like concat('%',:keyword,'%') " +
      "or g.goodsDescription like concat('%',:keyword,'%')) " +
      "and g.updatedAt < :cursor order by g.updatedAt desc")
  Slice<Goods> findAllByKeyword(@Param("keyword")String keyword,
                                @Param("cursor")Long cursor,
                                Pageable of);
  
  @Query("select g from Goods g where g.cateCd = :category " +
      "and (g.goodsNm like concat('%',:keyword,'%') " +
      "or g.goodsDescription like concat('%',:keyword,'%')) " +
      "and g.updatedAt < :cursor order by g.updatedAt desc")
  Slice<Goods> findAllByCategoryAndKeyword(@Param("category")String category,
                                           @Param("keyword")String keyword,
                                           @Param("cursor")Long cursor,
                                           Pageable of);
}