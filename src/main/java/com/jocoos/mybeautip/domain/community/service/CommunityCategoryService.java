package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCategoryService {

    private final CommunityCategoryRepository repository;
    private final CommunityCategoryConverter converter;

    public List<CommunityCategoryResponse> getCommunityCategoryList() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "sort"));
        List<CommunityCategory> categories = repository.findAllBy(pageable);

        return converter.convert(categories);
    }
}
