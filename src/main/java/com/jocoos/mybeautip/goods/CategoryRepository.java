package com.jocoos.mybeautip.goods;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, String> {
  
  Iterable<Category> findByGroup(String group);

  List<Category> findByGroup(String group, Sort sort);

}