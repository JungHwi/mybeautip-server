package com.jocoos.mybeautip.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.Trend;

@RepositoryRestResource(path = "posts", collectionResourceRel = "posts")
interface PostRepositoryForAdmin extends JpaRepository<Post, Long> {
}

@RepositoryRestResource(path = "trends", collectionResourceRel = "trends")
interface TrendRepositoryForAdmin extends JpaRepository<Trend, Long> {
}

@RepositoryRestResource(path = "banners", collectionResourceRel = "banners")
interface BannerRepositoryForAdmin extends JpaRepository<Banner, Long> {
}
