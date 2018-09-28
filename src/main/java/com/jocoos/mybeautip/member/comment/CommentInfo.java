package com.jocoos.mybeautip.member.comment;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import lombok.Data;

import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.mention.MentionTag;

@Data
public class CommentInfo {
  private Long id;
  private Long postId;
  private String comment;
  private Long parentId;
  private int commentCount;
  private MemberInfo createdBy;
  private Date createdAt;
  private String commentRef;
  private Long likeId;
  private Integer likeCount;
  private List<MentionTag> mentionInfo;

  public CommentInfo(Comment comment) {
    BeanUtils.copyProperties(comment, this);
    setCommentRef(comment);
    this.createdBy = new MemberInfo(comment.getCreatedBy(), null);
  }

  public CommentInfo(Comment comment, MemberInfo createdBy) {
    this(comment);
    this.createdBy = createdBy;
  }

  public CommentInfo(Comment comment, MemberInfo createdBy, List<MentionTag> mentionInfo) {
    this(comment, createdBy);
    this.mentionInfo = mentionInfo;
  }

  private void setCommentRef(Comment comment) {
    if (comment != null && comment.getCommentCount() > 0) {
      if (comment.getPostId() != null) {
        this.commentRef = String.format("/api/1/posts/%d/comments?parentId=%d", comment.getPostId(), comment.getId());
      }

      if (comment.getVideoId() != null) {
        this.commentRef = String.format("/api/1/videos/%d/comments?parentId=%d", comment.getVideoId(), comment.getId());
      }
    }
  }
}
