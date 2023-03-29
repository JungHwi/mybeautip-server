package com.jocoos.mybeautip.domain.search.service.impl;

import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.service.DomainSearchService;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.domain.vod.code.VodSortField;
import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.service.dao.VodDao;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.jocoos.mybeautip.domain.vod.code.VodStatus.AVAILABLE;
import static com.jocoos.mybeautip.global.code.SearchField.TITLE;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@Service
public class VodSearchService implements DomainSearchService<VodResponse> {

    private final VodDao vodDao;

    @Override
    public SearchType getType() {
        return SearchType.VOD;
    }

    @Override
    public SearchResponse<VodResponse> search(KeywordSearchRequest request) {
        VodSortField defaultSort = VodSortField.CREATED_AT;
        CursorPaging<Long> cursorPaging = CursorPaging.idCursorWithNonUniqueSortField(request.idCursor(), defaultSort.getSortField());
        Pageable pageable = PageRequest.of(0, request.size(), defaultSort.getSort(DESC));
        VodSearchCondition condition = VodSearchCondition.builder()
                .searchOption(SearchOption.onlyKeyword(TITLE, request.keyword()))
                .isVisible(true)
                .status(AVAILABLE)
                .cursorPaging(cursorPaging)
                .pageable(pageable)
                .build();
        Page<VodResponse> page = vodDao.getPageList(condition);
        return new SearchResponse<>(page.getContent(), page.getTotalElements()).contentJsonNameTo(getType());
    }

    @Override
    public long count(String keyword, @Nullable Member member) {
        VodSearchCondition condition = VodSearchCondition.builder()
                .searchOption(SearchOption.onlyKeyword(TITLE, keyword))
                .isVisible(true)
                .status(AVAILABLE)
                .build();
        return vodDao.count(condition);
    }
}
