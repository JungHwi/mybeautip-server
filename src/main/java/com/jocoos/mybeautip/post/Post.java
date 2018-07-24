package com.jocoos.mybeautip.post;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;

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
  private String bannerText;

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

  @OneToMany(cascade = CascadeType.ALL,
     orphanRemoval = true,
     fetch = FetchType.EAGER
  )
  @JoinColumn(name = "postId")
  @OrderColumn(name = "priority")
  private List<PostContent> contents;

  @OneToMany(cascade = CascadeType.ALL,
     orphanRemoval = true,
     fetch = FetchType.EAGER
  )
  @JoinColumn(name = "postId")
  @OrderColumn(name = "priority")
  private List<PostGoods> postGoods;


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

  public void addContent(PostContent content) {
    if (contents == null) {
      contents = Lists.newArrayList();
    }

    contents.add(content);
  }
}
