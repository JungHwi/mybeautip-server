package com.jocoos.mybeautip.store;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "stores")
public class Store {

  @Id
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @Column
  private String imageUrl;

  @Column
  private String thumbnailUrl;

  @Column(nullable = false)
  private int likeCount;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;

  public Store(Long scmNo) {
    this.setId(scmNo);
  }
}