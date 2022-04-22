package com.jocoos.mybeautip.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;

@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @Transactional
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void firstLike() {
        Post post = postRepository.getById(2L);
        postService.likePost(post, 4L);
    }

    @Test
    @Transactional
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void twentyLike() {
        Post post = postRepository.getById(3L);
        postService.likePost(post, 4L);
    }
}