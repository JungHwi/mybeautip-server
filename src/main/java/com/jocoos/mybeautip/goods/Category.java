package com.jocoos.mybeautip.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  
  @Column(name = "category_name")
  private String name;

  @Column
  private String thumbnailUrl;
}