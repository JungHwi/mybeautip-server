package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "post_comment_details", types = Comment.class)
public interface PostCommentExceprt {

    Long getId();

    @Value("#{target.createdBy}")
    Member getMember();

    String getComment();

    Long getPostId();

    Long getVideoId();

    Long getParentId();

    int getCommentCount();

    int getLikeCount();

    Date getCreatedAt();
}
