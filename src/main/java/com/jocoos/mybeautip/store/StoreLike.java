package com.jocoos.mybeautip.store;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "store_likes")
public class StoreLike {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name="store_id")
  private Store store;

  @Column(nullable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public StoreLike(Store store) {
    this.store = store;
  }
}