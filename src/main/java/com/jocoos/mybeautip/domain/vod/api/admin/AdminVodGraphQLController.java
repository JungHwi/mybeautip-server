package com.jocoos.mybeautip.domain.vod.api.admin;

import com.jocoos.mybeautip.domain.vod.dto.PagingFilter;
import com.jocoos.mybeautip.domain.vod.dto.VodFilter;
import com.jocoos.mybeautip.domain.vod.dto.VodInput;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.persistence.domain.VodReport;
import com.jocoos.mybeautip.domain.vod.service.AdminVodGraphqlService;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.vod.code.VodStatus.AVAILABLE;

@RequiredArgsConstructor
@Controller
public class AdminVodGraphQLController {

    private final AdminVodGraphqlService service;

    @QueryMapping
    public List<Vod> vodList(@Argument("paging") PagingFilter pagingFilter,
                             @Argument("filter") VodFilter vodFilter) {
        VodSearchCondition condition = VodSearchCondition.builder()
                .pageable(pagingFilter.pageable())
                .status(AVAILABLE)
                .searchOption(vodFilter == null ? null : vodFilter.searchOption())
                .isVisible(vodFilter == null ? null : vodFilter.isVisible())
                .build();
        return service.getList(condition);
    }

    @QueryMapping
    public Vod vodById(@Argument long id) {
        return service.get(id);
    }

    @MutationMapping
    public Vod editVod(@Argument VodInput vodInput) {
        return service.edit(vodInput);
    }

    @BatchMapping(typeName = "Vod", field = "reports")
    public Map<Vod, List<VodReport>> reports(List<Vod> vodList) {
        return service.getVodReportsMap(vodList);
    }

    @BatchMapping(typeName = "VodReport", field = "reporter")
    public Map<VodReport, Member> reporter(List<VodReport> reports) {
        return service.getVodReportReporterMap(reports);
    }

    @BatchMapping(typeName = "Vod", field = "member")
    public Map<Vod, Member> member(List<Vod> vodList) {
        return service.getVodMemberMap(vodList);
    }
}

