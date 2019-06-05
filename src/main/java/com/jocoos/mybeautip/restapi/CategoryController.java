package com.jocoos.mybeautip.restapi;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Category;
import com.jocoos.mybeautip.goods.CategoryGroup;
import com.jocoos.mybeautip.goods.CategoryRepository;

@Slf4j
@RestController
@RequestMapping("/api/1/categories")
public class CategoryController {
  private final CategoryRepository categoryRepository;
  private static final String GOODS_CATEGORY_TOP = "0";

  public CategoryController(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @GetMapping
  public ResponseEntity<List<CategoryGroup>> getAllCategories() {
    List<CategoryGroup> groups = new ArrayList<>();

    Sort sort = Sort.by(Sort.Direction.DESC, "seq");
    Iterable<Category> iterable = categoryRepository.findByGroup(GOODS_CATEGORY_TOP, sort);
    List<Category> subCategories;
    CategoryGroup categoryGroup;
    Iterable<Category> subIterable;

    for (Category c : iterable) {
      subIterable = categoryRepository.findByGroup(c.getCode());
      subCategories = new ArrayList<>();
      subIterable.forEach(subCategories::add);

      categoryGroup = new CategoryGroup();
      categoryGroup.setCode(c.getCode());
      categoryGroup.setName(c.getName());
      categoryGroup.setThumbnailUrl(c.getThumbnailUrl());
      categoryGroup.setSubs(subCategories);

      groups.add(categoryGroup);
    }
    return new ResponseEntity<>(groups, HttpStatus.OK);
  }

  @PatchMapping("/{id:.+}")
  public Category updateCategoryThumbnailImage(@PathVariable String id) {
    log.info("updateCategoryThumbnailImage called: " + id);
    return categoryRepository.findById(id)
      .map(category -> {
        String url = StringUtils.substringBefore(category.getThumbnailUrl(), "?");
        category.setThumbnailUrl(String.format("%s?time=%s", url, System.currentTimeMillis()));
        log.info("updateCategoryThumbnailImage changed: " + category.getThumbnailUrl());
        return categoryRepository.save(category);
      })
      .orElseThrow(() -> new NotFoundException("category_not_found", "category not found:" + id));
  }

  @Data
  static class UpdateCategoryRequest {
    @Size(max=255)
    @NotNull
    private String thumbnailUrl;
  }
}