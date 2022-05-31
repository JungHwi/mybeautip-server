package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.post.PostLike;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "post_like_details", types = PostLike.class)
public interface PostLikeExcerpt {

    Long getId();

    @Value("#{target.createdBy}")
    Member getMember();

    Date getCreatedAt();

}
