package com.jocoos.mybeautip.post;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String thumbnailUrl;

  @Column(nullable = false)
  private int category;

  @Column(nullable = false)
  private Long viewCount = 0l;

  @Column(nullable = false)
  private Long likeCount = 0l;

  @Column(nullable = false)
  private Long commentCount = 0l;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
     name = "post_contents",
     joinColumns = @JoinColumn(name = "post_id")
  )
  @AttributeOverrides({
     @AttributeOverride(name = "category", column = @Column(name = "category")),
     @AttributeOverride(name = "content", column = @Column(name = "content"))
  })
  @OrderBy("seq")
  private Set<PostContent> contents;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
     name = "post_goods",
     joinColumns = @JoinColumn(name = "post_id")
  )
  @OrderColumn(name = "seq")
  @Column(name = "goods_no")
  private List<String> goods;

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
  private Date deletedAt;
}
