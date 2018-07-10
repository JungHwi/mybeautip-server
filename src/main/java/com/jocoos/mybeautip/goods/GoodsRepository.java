package com.jocoos.mybeautip.goods;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends CrudRepository<Goods, String> {
	
	@Query("select g from Goods g where g.updatedAt <= ?1 order by g.updatedAt desc")
	Slice<Goods> getGoodsList(Long cursor, Pageable pageable);
	
	@Query("select g from Goods g where g.cateCd = ?1 and g.updatedAt <= ?2 order by g.updatedAt desc")
	Slice<Goods> findAllByCategory(String category, long startCursor, Pageable pageable);
}