package com.jocoos.mybeautip.godo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1/godo")
public class GodoController {
  @Autowired
  private GodoService godoService;
  
  @GetMapping("/category")
  public void getCategoriesFromGodo() {
    godoService.getCategoriesFromGodo();
  }
  
  @GetMapping("/goods")
  public void getGoodsList() {
    godoService.getGoodsFromGodo();
  }
}