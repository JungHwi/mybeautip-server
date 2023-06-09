package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
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

    public List<CommunityCategoryResponse> getLowerCategoryList(CommunityCategoryType type) {
        CommunityCategory category = repository.findByType(type)
                .orElseThrow(() -> new BadRequestException("CommunityCategoryType is not supported. type - " + type));
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "sort"));
        return converter.convert(repository.findAllByParentId(category.getId(), pageable));
    }

    public CommunityCategoryResponse getCommunityCategory(long categoryId) {
        CommunityCategory communityCategory = repository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Request Id is - " + categoryId));
        return converter.convert(communityCategory);
    }
}
