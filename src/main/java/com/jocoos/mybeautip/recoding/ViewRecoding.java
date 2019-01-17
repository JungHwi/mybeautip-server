package com.jocoos.mybeautip.recoding;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "view_recodings")
public class ViewRecoding extends MemberAuditable {

  public static final int CATEGORY_POST = 1;
  public static final int CATEGORY_GOODS = 2;
  public static final int CATEGORY_VIDEO = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String itemId;

  /**
   * 1. Post, 2. Goods, 3. Video
   */
  @Column(nullable = false)
  private int category;
  
  @Column(nullable = false)
  private int viewCount;
  
  @Column
  @LastModifiedDate
  private Date modifiedAt;

  public ViewRecoding(String itemId, int category) {
    this.itemId = itemId;
    this.category = category;
    this.viewCount = 1; // default value
  }
}
