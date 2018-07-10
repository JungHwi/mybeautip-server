package com.jocoos.mybeautip.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "goods_categories")
@Data
public class Category {
	@Id
	private String code;
	
	@JsonIgnore
	@Column(name = "parent_code")
	private String group;
	
	@Column(name = "category_name")
	private String name;
	
	@JsonIgnore
	private String displayOnPc;
	@JsonIgnore
	private String displayOnMobile;
}