package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.goods.CategoryGroup;
import com.jocoos.mybeautip.goods.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/1/categories")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;
  
  @GetMapping
  public ResponseEntity getAllCategories() {
    List<CategoryGroup> allCategories = categoryService.getGoodsCategories();
    return new ResponseEntity(allCategories, HttpStatus.OK);
  }
}