package com.jocoos.mybeautip.goods;

import lombok.Data;

import java.util.List;

@Data
public class CategoryGroup {
    private String code;
    private String name;
    private String thumbnailUrl;
    private List<Category> subs;
}