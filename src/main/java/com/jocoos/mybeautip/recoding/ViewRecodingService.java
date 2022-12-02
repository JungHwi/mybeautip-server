package com.jocoos.mybeautip.recoding;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class ViewRecodingService {

    private static final int DAY_IN_MS = 1000 * 60 * 60 * 24;
    private static final int MAX_COUNT = 200;

    private final ViewRecodingRepository viewRecodingRepository;
//    private final PostRepository postRepository;

    public ViewRecodingService(ViewRecodingRepository viewRecodingRepository
//                               ,PostRepository postRepository
    ) {
        this.viewRecodingRepository = viewRecodingRepository;
//        this.postRepository = postRepository;
    }

    public Slice<ViewRecoding> findByWeekAgo(Long memberId, int count, String cursor, Integer category) {
        if (count > MAX_COUNT) {
            throw new BadRequestException("The count must be less or equals to 200");
        }

        PageRequest page = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "modifiedAt"));

        Date now;
        if (!StringUtils.isBlank(cursor)) {
            now = new Date(Long.parseLong(cursor));
        } else {
            now = new Date();
        }

        Date weekAgo = new Date(now.getTime() - 7 * DAY_IN_MS);
        if (category == null) {
            return viewRecodingRepository.findByCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(memberId, now, weekAgo, page);
        } else {
            return viewRecodingRepository.findByCategoryAndCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(category, memberId, now, weekAgo, page);
        }
    }

    @Transactional
    public void insertOrUpdate(String itemId, int category, Member me) {
//        if (category == ViewRecoding.CATEGORY_POST) {
//            Post post = postRepository.findByIdAndDeletedAtIsNull(Long.parseLong(itemId))
//                    .orElseThrow(() -> new NotFoundException("Post not found: " + itemId));
//            if (post.getCategory() == Post.CATEGORY_NOTICE) {
//                return; // Do not insert view log when post type is 'notice'
//            }
//        }

        viewRecodingRepository.findByItemIdAndCategoryAndCreatedBy(itemId, category, me)
                .map(recoding -> {
                    recoding.setViewCount(recoding.getViewCount() + 1);
                    viewRecodingRepository.save(recoding);
                    return Optional.empty();
                })
                .orElseGet(() -> {
                    viewRecodingRepository.save(new ViewRecoding(itemId, category));
                    return Optional.empty();
                });
    }
}
