package com.jocoos.mybeautip.goods;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	
	private static final String GOODS_CATEGORY_TOP = "0";
	
	public List<CategoryGroup> getGoodsCategories() {
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
			categoryGroup.setSubs(subCategories);
			
			groups.add(categoryGroup);
		}
		return groups;
	}
}