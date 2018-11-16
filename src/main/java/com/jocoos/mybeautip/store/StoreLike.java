package com.jocoos.mybeautip.store;

import com.jocoos.mybeautip.audit.MemberAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "store_likes")
public class StoreLike extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name="store_id")
  private Store store;

  public StoreLike(Store store) {
    this.store = store;
  }
}