//package com.jocoos.mybeautip.post;
//
//import com.jocoos.mybeautip.global.code.LikeStatus;
//import com.jocoos.mybeautip.global.exception.BadRequestException;
//import com.jocoos.mybeautip.global.exception.ErrorCode;
//import com.jocoos.mybeautip.global.exception.NotFoundException;
//import com.jocoos.mybeautip.member.LegacyMemberService;
//import com.jocoos.mybeautip.member.Member;
//import com.jocoos.mybeautip.member.block.Block;
//import com.jocoos.mybeautip.member.block.BlockRepository;
//import com.jocoos.mybeautip.member.comment.*;
//import com.jocoos.mybeautip.member.mention.MentionResult;
//import com.jocoos.mybeautip.member.mention.MentionService;
//import com.jocoos.mybeautip.notification.MessageService;
//import com.jocoos.mybeautip.support.AttachmentService;
//import com.jocoos.mybeautip.support.DigestUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Isolation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.jocoos.mybeautip.global.code.LikeStatus.LIKE;
//import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PostService {
//
//    private static final String COMMENT_BLOCKED_MESSAGE = "comment.blocked_message";
//
//    private static final int CONTENT_LENGTH_POINT_RESTRICTION = 30;
//    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;
//    private final MentionService mentionService;
//    private final CommentService commentService;
//    private final LegacyMemberService legacyMemberService;
//    private final CommentLikeRepository commentLikeRepository;
//    private final PostLikeRepository postLikeRepository;
//    private final PostReportRepository postReportRepository;
//    private final BlockRepository blockRepository;
//    private final AttachmentService attachmentService;
//    private final MessageService messageService;
//
//    public Slice<Comment> findCommentsByPostId(Long id, Long cursor, Pageable pageable, String sort) {
//        Slice<Comment> comments;
//        if (cursor != null) {
//            if ("next".equals(sort)) {
//                comments = commentRepository.findByPostIdAndIdGreaterThanEqualAndParentIdIsNull(id, cursor, pageable);
//            } else {
//                comments = commentRepository.findByPostIdAndIdLessThanEqualAndParentIdIsNull(id, cursor, pageable);
//            }
//        } else {
//            comments = commentRepository.findByPostIdAndParentIdIsNull(id, pageable);
//        }
//        return comments;
//    }
//
//    public CommentInfo getPostComment(Long postId, Long id, Member me, String lang) {
//        Comment comment = commentRepository.findByIdAndPostId(id, postId)
//                .orElseThrow(() -> new NotFoundException("No such Comment. postId - " + postId + ", commentId - " + id));
//
//        CommentInfo commentInfo = new CommentInfo(comment, legacyMemberService.getMemberInfo(comment.getCreatedBy()));
//        boolean isChangeableComment = true;
//
//        String content = commentService.getBlindContent(comment, lang, null);
//        if (me != null) {
//            Long likeId = commentLikeRepository.findByCommentIdAndCreatedByIdAndStatus(comment.getId(), me.getId(), LIKE)
//                    .map(CommentLike::getId).orElse(null);
//            commentInfo.setLikeId(likeId);
//
//            Block block = blockRepository
//                    .findByMeAndMemberYouIdAndStatus(me.getId(), comment.getCreatedBy().getId(), BLOCK)
//                    .orElse(null);
//            if (block != null) {
//                commentInfo.setBlockId(block.getId());
//                commentInfo.setComment(messageService.getMessage(COMMENT_BLOCKED_MESSAGE, lang));
//                isChangeableComment = false;
//            }
//        }
//
//        if (isChangeableComment && !comment.getComment().equals(content)) {
//            commentInfo.setComment(content);
//            isChangeableComment = false;
//        }
//
//        if (isChangeableComment && comment.getComment().contains("@")) {
//            MentionResult mentionResult = mentionService.createMentionComment(comment.getComment());
//            commentInfo.setComment(mentionResult.getComment());
//        }
//
//        return commentInfo;
//    }
//
//    public Slice<Comment> findCommentsByParentId(Long parentId, Long cursor, Pageable pageable, String sort) {
//        Slice<Comment> comments;
//        if (cursor != null) {
//            if ("next".equals(sort)) {
//                comments = commentRepository.findByParentIdAndIdGreaterThanEqual(parentId, cursor, pageable);
//            } else {
//                comments = commentRepository.findByParentIdAndIdLessThanEqual(parentId, cursor, pageable);
//            }
//        } else {
//            comments = commentRepository.findByParentId(parentId, pageable);
//        }
//        return comments;
//    }
//
//    public String getPostCategoryName(int category) {
//        switch (category) {
//            case 1:
//                return "post.category_trend";
//            case 2:
//                return "post.category_cardnews";
//            case 3:
//                return "post.category_event";
//            case 4:
//                return "post.category_notice";
//            case 5:
//                return "post.category_motd";
//            case 6:
//                return "post.category_curation";
//            default:
//                return "post";
//        }
//    }
//
//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public PostLike likePost(Post post, long memberId) {
//        postRepository.updateLikeCount(post.getId(), 1);
//        post.setLikeCount(post.getLikeCount() + 1);
//        PostLike postLike = postLikeRepository.findByPostIdAndCreatedById(post.getId(), memberId)
//                .orElse(new PostLike(post));
//        postLike.setStatus(LIKE);
//
//        PostLike savedLike = postLikeRepository.save(postLike);
//        return savedLike;
//    }
//
//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public void unLikePost(PostLike liked) {
//        liked.setStatus(LikeStatus.UNLIKE);
//        postLikeRepository.save(liked);
//        if (liked.getPost().getLikeCount() > 0) {
//            postRepository.updateLikeCount(liked.getPost().getId(), -1);
//        }
//    }
//
//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public CommentLike likeCommentPost(Long commentId, Long postId, Member member) {
//
//        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
//                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_LIKE_NOT_FOUND, "invalid post or comment id"));
//        commentRepository.updateLikeCount(comment.getId(), 1);
//
//        CommentLike commentLike = commentLikeRepository.findByCommentIdAndCreatedById(comment.getId(), member.getId())
//                .orElse(new CommentLike(comment));
//
//        if (LIKE.equals(commentLike.getStatus())) {
//            throw new BadRequestException("already liked");
//        }
//
//        commentLike.like();
//        commentLikeRepository.save(commentLike);
//        return commentLike;
//    }
//
//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public void unLikeCommentPost(CommentLike liked) {
//        liked.unlike();
//        if (liked.getComment().getCommentCount() > 0) {
//            commentRepository.updateLikeCount(liked.getComment().getId(), -1);
//        }
//    }
//
//    @Transactional
//    public void updateViewCount(Post post, int i) {
//        postRepository.updateViewCount(post.getId(), i);
//    }
//
//    @Transactional
//    public Post reportPost(Post post, Member me, int reasonCode, String reason) {
//        postReportRepository.save(new PostReport(post, me, reasonCode, reason));
//        post.setReportCount(post.getReportCount() + 1);
//        return postRepository.save(post);
//    }
//
//    @Transactional
//    public Post savePost(Post post, List<MultipartFile> files, Member member) {
//        postRepository.save(post);
//
//        List<String> attachments = null;
//        try {
//            String keyPath = String.format("posts/%s", post.getId());
//            attachments = attachmentService.upload(files, keyPath);
//        } catch (IOException e) {
//            throw new BadRequestException("state_required");
//        }
//
//        if (attachments != null && attachments.size() > 0) {
//            int seq = 0;
//            Set<PostContent> contents = new HashSet<>();
//            for (String attachment : attachments) {
//                contents.add(new PostContent(seq++, PostContent.ContentCategory.IMAGE, attachment));
//
//            }
//            post.setContents(contents);
//            postRepository.save(post);
//        }
//
//        return post;
//    }
//
//    @Transactional
//    public Post updatePost(Post post, List<MultipartFile> files) {
//        List<String> attachments = new ArrayList<>();
//        String keyPath = String.format("posts/%s", post.getId());
//
//        Set<PostContent> contents = post.getContents();
//        Map<String, PostContent> contentMap = new HashMap<>();
//        contents.stream().forEach(c ->
//                contentMap.put(getFilename(c.getContent()), c));
//
//        if (files != null) {
//            for (MultipartFile file : files) {
//                String filename = DigestUtils.getFilename(file);
//                if (contentMap.containsKey(filename)) {
//                    PostContent uploadedContent = contentMap.get(filename);
//                    attachments.add(uploadedContent.getContent());
//                    contents.remove(uploadedContent);
//                } else {
//                    try {
//                        attachments.add(attachmentService.upload(file, keyPath));
//                    } catch (IOException e) {
//                        throw new BadRequestException("image_upload_fail");
//                    }
//                }
//            }
//        }
//
//        if (contents.size() > 0) {
//            post.setContents(null);
//            postRepository.save(post);
//
//            log.debug("{}", contents);
//            clearResource(contents);
//        }
//
//        if (attachments != null && attachments.size() > 0) {
//            int seq = 0;
//            Set<PostContent> newContents = new HashSet<>();
//            for (String attachment : attachments) {
//                newContents.add(new PostContent(seq++, PostContent.ContentCategory.IMAGE, attachment));
//            }
//            post.setContents(newContents);
//            return postRepository.save(post);
//        } else {
//            return post;
//        }
//    }
//
//    private String getFilename(String path) {
//        if (!StringUtils.isBlank(path)) {
//            String[] split = path.split("/");
//            if (split.length > 0) {
//                return split[split.length - 1];
//            }
//        }
//        return "";
//    }
//
//    private void clearResource(Set<PostContent> contents) {
//        List<String> keys = contents.stream()
//                .map(c -> getPath(c.getContent())).collect(Collectors.toList());
//        attachmentService.deleteAttachments(keys);
//    }
//
//    private String getPath(String content) {
//        try {
//            URI uri = new URI(content);
//            return uri.getPath();
//        } catch (URISyntaxException e) {
//            log.error("{}", e);
//        }
//        return null;
//    }
//
//    @Transactional
//    public void removePost(Post post) {
//        post.setDeletedAt(new Date());
//        postRepository.save(post);
//    }
//}
