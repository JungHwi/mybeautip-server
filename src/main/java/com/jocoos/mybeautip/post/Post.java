package com.jocoos.mybeautip.post;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
  private String description;

  @Column(nullable = false)
  private String thumbnailUrl;

  /**
   * 1: trend, 2: card news, 3: event, 4: notices, 5: MOTD
   */
  @Column(nullable = false)
  private int category;

  /**
   * Event progress
   * 0: default(no event), 1: in progress 2: end
   */
  @Column(nullable = false)
  private int progress;

  @Column(nullable = false)
  private int viewCount;

  @Column(nullable = false)
  private int likeCount;

  @Column(nullable = false)
  private int commentCount;

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
     name = "post_winners",
     joinColumns = @JoinColumn(name = "post_id")
  )
  @Column(name = "member_id")
  private Set<Long> winners;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
     name = "post_goods",
     joinColumns = @JoinColumn(name = "post_id")
  )
  @OrderColumn(name = "seq")
  @Column(name = "goods_no")
  private List<String> goods;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private Member creator;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;
}
