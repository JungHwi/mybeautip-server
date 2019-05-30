package com.jocoos.mybeautip.goods;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Data
@Entity
@Table(name = "goods_categories")
public class Category {
  @Id
  private String code;
  
  @Column(name = "parent_code")
  private String group;

  @Column
  private int seq;

  @Column(name = "category_name")
  private String name;

  @Column
  private String thumbnailUrl;
}