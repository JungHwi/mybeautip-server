package com.jocoos.mybeautip.store;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "stores")
public class Store extends ModifiedDateAuditable {
  @Id
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @Column
  private String centerPhone;
  
  @Column
  private String imageUrl;

  @Column
  private String thumbnailUrl;

  @Column
  private String refundUrl;

  @Column
  private String asUrl;

  @Column
  private String deliveryInfo;

  @Column
  private String cancelInfo;

  @Column(nullable = false)
  private int likeCount;

  @Column
  private Date deletedAt;

  public Store(Integer scmNo) {
    this.setId(scmNo);
  }
}