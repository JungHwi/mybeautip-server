package com.jocoos.mybeautip.recoding;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
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

  public ViewRecoding(String itemId, int category) {
    this.itemId = itemId;
    this.category = category;
  }
}
