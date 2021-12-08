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
  private Boolean locked = false;

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

  /**
   * 0: Default, 1: Deleted 2: Blinded, 4: Blinded by Admin
   */
  @Column
  private int state;

  @Column(nullable = false)
  @LastModifiedDate
  private Date modifiedAt;



  public enum CommentState {
    DEFAULT(0),
    DELETED(1),
    BLINDED(2),
    BLINDED_BY_ADMIN(4);

    private int value;
    CommentState(int value) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }
  }

  public void setState(CommentState commentState) {
    this.state = commentState.value();
  }
}
