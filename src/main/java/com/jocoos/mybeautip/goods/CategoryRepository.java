package com.jocoos.mybeautip.goods;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, String> {

    Iterable<Category> findByGroup(String group);

    List<Category> findByGroup(String group, Sort sort);

}