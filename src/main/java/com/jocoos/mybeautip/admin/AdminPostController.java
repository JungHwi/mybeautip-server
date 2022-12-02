//package com.jocoos.mybeautip.admin;
//
//import com.jocoos.mybeautip.post.Post;
//import com.jocoos.mybeautip.post.PostRepository;
//import com.jocoos.mybeautip.restapi.PostController;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/admin/manual/posts")
//public class AdminPostController {
//
//    private final PostRepository postRepository;
//
//    public AdminPostController(PostRepository postRepository) {
//        this.postRepository = postRepository;
//    }
//
//    @GetMapping
//    public ResponseEntity<Page<PostController.PostInfo>> getPosts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String sort) {
//
//        Pageable pageable = null;
//        if (sort != null) {
//            Sort pagingSort = null;
//            switch (sort) {
//                case "like":
//                    pagingSort = Sort.by(Sort.Direction.DESC, "likeCount");
//                    break;
//                case "view":
//                    pagingSort = Sort.by(Sort.Direction.DESC, "viewCount");
//                    break;
//                case "comment":
//                    pagingSort = Sort.by(Sort.Direction.DESC, "commentCount");
//                    break;
//                default:
//                    pagingSort = Sort.by(Sort.Direction.DESC, "id");
//            }
//
//            pageable = PageRequest.of(page, size, pagingSort);
//        } else {
//            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
//        }
//
//        Page<Post> posts = postRepository.findByDeletedAtIsNull(pageable);
//
//        Page<PostController.PostInfo> details = posts.map(m -> new PostController.PostInfo(m));
//        return new ResponseEntity<>(details, HttpStatus.OK);
//    }
//}
