package com.jocoos.mybeautip.post;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "trends")
public class Trend {

  @Id
  @Column(name = "post_id")
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(
     name = "post_id",
     referencedColumnName = "id",
     foreignKey = @ForeignKey(name = "fk_trends_posts")
  )
  private Post post;

  @Column(nullable = false)
  private int seq;

  @Column(nullable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;

}
