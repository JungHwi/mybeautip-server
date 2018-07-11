package com.jocoos.mybeautip.goods;

import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, String> {
  
  Iterable<Category> findByGroup(String group);
}