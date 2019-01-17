package com.jocoos.mybeautip.post;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.banner.Banner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "posts")
public class Post extends MemberAuditable {

  public static final int CATEGORY_TREND = 1;
  public static final int CATEGORY_CARDNEWS = 2;
  public static final int CATEGORY_EVENT = 3;
  public static final int CATEGORY_NOTICE = 4;
  public static final int CATEGORY_MOTD = 5;
  public static final int CATEGORY_CURATION = 6;
  

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
   * 1: trend, 2: card news, 3: event, 4: notices, 5: MOTD, 6. curation
   */
  @Column(nullable = false)
  private int category;

  /**
   * Show post to users whether or not
   */
  @Column(nullable = false)
  private boolean opened;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;

  /**
   * Event progress
   * 0: default(no event), 1: 진행중 2: 선정중 3: 발표 4:종료
   */
  @Column(nullable = false)
  private int progress;

  @Column(nullable = false)
  private int viewCount;

  @Column(nullable = false)
  private int likeCount;

  @Column(nullable = false)
  private int commentCount;

  @OneToOne(mappedBy = "post")
  private Banner banner;

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
  
  @Column
  private String tagInfo;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;
}
