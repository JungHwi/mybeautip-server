package com.jocoos.mybeautip.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostContent;
import com.jocoos.mybeautip.post.PostGoods;

@RepositoryRestResource(path = "posts", collectionResourceRel = "posts")
interface PostRepositoryForAdmin extends JpaRepository<Post, Long> {
}

@RepositoryRestResource(path = "post_contents", collectionResourceRel = "post_contents")
interface PostContentRepositoryForAdmin extends JpaRepository<PostContent, Long> {
}

@RepositoryRestResource(path = "post_goods", collectionResourceRel = "post_goods")
interface PostGoodsRepositoryForAdmin extends JpaRepository<PostGoods, Long> {
}
