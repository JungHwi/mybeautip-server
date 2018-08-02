package com.jocoos.mybeautip.recoding;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "view_recodings")
public class ViewRecoding {

  public static final int CATEGORY_POST = 1;
  public static final int CATEGORY_GOODS = 2;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String itemId;

  /**
   * 1. Post, 2. Goods
   */
  @Column(nullable = false)
  private int category;

  @Column(nullable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public ViewRecoding(String itemId, int category) {
    this.itemId = itemId;
    this.category = category;
  }
}
