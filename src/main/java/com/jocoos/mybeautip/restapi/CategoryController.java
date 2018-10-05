package com.jocoos.mybeautip.restapi;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Category;
import com.jocoos.mybeautip.goods.CategoryGroup;
import com.jocoos.mybeautip.goods.CategoryRepository;

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
    Iterable<Category> iterable = categoryRepository.findByGroup(GOODS_CATEGORY_TOP);
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
    return categoryRepository.findById(id)
      .map(category -> {
        String url = StringUtils.substringBefore(category.getThumbnailUrl(), "?");
        category.setThumbnailUrl(String.format("%s?time=%s", url, System.currentTimeMillis()));
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