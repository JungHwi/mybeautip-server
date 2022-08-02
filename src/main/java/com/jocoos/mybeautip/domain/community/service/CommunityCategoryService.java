package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCategoryService {

    public List<CommunityCategoryResponse> getCommunityCategoryList() {
        List<CommunityCategoryResponse> result = new ArrayList<>();

        CommunityCategoryResponse total = CommunityCategoryResponse.builder()
                .id(1L)
                .type(CommunityCategoryType.NORMAL)
                .title("전체")
                .build();
        result.add(total);

        CommunityCategoryResponse blind = CommunityCategoryResponse.builder()
                .id(2L)
                .type(CommunityCategoryType.BLIND)
                .title("속닥속닥🙈")
                .build();
        result.add(blind);

        CommunityCategoryResponse drip = CommunityCategoryResponse.builder()
                .id(3L)
                .type(CommunityCategoryType.DRIP)
                .title("드립N드림")
                .build();
        result.add(drip);

        CommunityCategoryResponse review = CommunityCategoryResponse.builder()
                .id(4L)
                .type(CommunityCategoryType.NORMAL)
                .title("내돈내산")
                .build();
        result.add(review);

        return result;
    }
}
