package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, String> {
  
  @Query("select c from Category c where c.group = ?1")
  Iterable<Category> findByGroup(String group);
}