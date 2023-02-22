package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.VodReport;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodReportDao;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.dto.CountResponse;
import com.jocoos.mybeautip.global.dto.IsVisibleResponse;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.exception.ErrorCode.ALREADY_REPORT;

@RequiredArgsConstructor
@Service
public class VodService {

    private final VodDao vodDao;
    private final MemberDao memberDao;
    private final VodReportDao reportDao;
    private final BroadcastCategoryDao categoryDao;

    @Transactional(readOnly = true)
    public List<VodResponse> getList(long categoryId, CursorPaging<Long> cursorPaging) {
        VodSearchCondition condition = VodSearchCondition.builder()
                .categoryIds(getCategories(categoryId))
                .cursorPaging(cursorPaging)
                .build();
        return vodDao.getListWithMember(condition);
    }

    @Transactional(readOnly = true)
    public VodResponse get(long vodId) {
        Vod vod = vodDao.get(vodId);
        Member member = memberDao.getMember(vod.getMemberId());
        return new VodResponse(vod, member);
    }

    @Transactional
    public IsVisibleResponse changeVodVisibility(long id, boolean isVisible) {
        Vod vod = vodDao.get(id);
        vod.visible(isVisible);
        return new IsVisibleResponse(vod.getId(), vod.isVisible());
    }

    @Transactional
    public CountResponse report(long vodId, long reporterId, String description) {
        validIsFirstReport(vodId, reporterId);
        Vod reportedVod = vodDao.getForUpdate(vodId);
        // TODO Report should save before select for update
        VodReport vodReport = new VodReport(reportedVod, reporterId, description);
        reportDao.save(vodReport);
        reportedVod.addReportCount(1);

        // TODO Response values need to be discussed
        return new CountResponse(reportedVod.getId(), reportedVod.getReportCount());
    }

    // TODO Need to use Redis or DynamoDb for concurrency control of many requests
    @Transactional
    public CountResponse addHeartCount(long vodId, int addCount) {
        Vod vod = vodDao.getForUpdate(vodId);
        vod.addHeartCount(addCount);
        return new CountResponse(vod.getId(), vod.getTotalHeartCount());
    }

    private List<Long> getCategories(long categoryId) {
        return categoryDao.getCategories(categoryId)
                .stream()
                .map(BroadcastCategory::getId)
                .toList();
    }

    private void validIsFirstReport(long vodId, long reporterId) {
        if (reportDao.exist(vodId, reporterId)) {
            throw new BadRequestException(ALREADY_REPORT);
        }
    }
}
