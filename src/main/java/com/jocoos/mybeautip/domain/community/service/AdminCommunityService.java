package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.converter.AdminCommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NOT_IN_ADMIN;

@RequiredArgsConstructor
@Service
public class AdminCommunityService {

    private final CommunityCategoryDao categoryDao;
    private final AdminCommunityConverter converter;

    @Transactional(readOnly = true)
    public List<CommunityCategoryResponse> getCategories() {
        List<CommunityCategory> adminCategories = categoryDao.getAllExcludeByTypes(NOT_IN_ADMIN);
        return converter.convert(adminCategories);
    }
}
