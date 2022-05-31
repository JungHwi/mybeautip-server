package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberMeInfo;
import com.jocoos.mybeautip.member.mention.MentionTag;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Set;

@Data
public class CommentInfo {
    private Long id;
    private Long postId;
    private Long videoId;
    private Boolean locked;
    private String comment;
    private Long parentId;
    private int commentCount;
    private MemberInfo createdBy;
    private Date createdAt;
    private String commentRef;
    private Long likeId;
    private Long blockId;
    private Integer likeCount;
    private Integer reportCount;
    private Integer state;
    private Set<MentionTag> mentionInfo;

    public CommentInfo(Comment comment) {
        BeanUtils.copyProperties(comment, this);
        setCommentRef(comment);
        this.createdBy = new MemberMeInfo(comment.getCreatedBy());
    }

    public CommentInfo(Comment comment, MemberInfo createdBy) {
        this(comment);
        this.createdBy = createdBy;
    }

    public CommentInfo(Comment comment, MemberInfo createdBy, Set<MentionTag> mentionInfo) {
        this(comment, createdBy);
        this.mentionInfo = mentionInfo;
    }

    private void setCommentRef(Comment comment) {
        if (comment != null && comment.getCommentCount() > 0) {
            if (comment.getPostId() != null) {
                this.commentRef = String.format("/api/1/posts/%d/comments?parent_id=%d", comment.getPostId(), comment.getId());
            }

            if (comment.getVideoId() != null) {
                this.commentRef = String.format("/api/1/videos/%d/comments?parent_id=%d", comment.getVideoId(), comment.getId());
            }
        }
    }
}
