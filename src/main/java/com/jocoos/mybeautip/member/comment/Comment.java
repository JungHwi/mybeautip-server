package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.audit.MemberAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "comments")
public class Comment extends MemberAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long postId;

  @Column
  private Long videoId;
  
  @Column
  private Boolean locked;

  @Column(nullable = false)
  private String comment;
  
  @Column
  private String originalComment;
  
  @Column
  private Long parentId;

  @Column
  private int commentCount;

  @Column
  private int likeCount;

  @Column(nullable = false)
  @LastModifiedDate
  private Date modifiedAt;
}
