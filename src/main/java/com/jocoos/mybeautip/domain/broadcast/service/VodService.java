package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.dto.HeartCountResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.VodReport;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodReportDao;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.dto.ReportCountResponse;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;
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
                .isVisible(true)
                .build();
        return vodDao.getListWithMember(condition);
    }

    @Transactional(readOnly = true)
    public VodResponse get(long vodId) {
        Vod vod = vodDao.get(vodId);
        if (!vod.canWatch()) {
            throw new BadRequestException(ACCESS_DENIED, "vod is not watchable");
        }
        Member member = memberDao.getMember(vod.getMemberId());
        return new VodResponse(vod, member);
    }

    @Transactional
    public ReportCountResponse report(long vodId, long reporterId, String description) {
        validIsFirstReport(vodId, reporterId);
        Vod reportedVod = vodDao.get(vodId);
        VodReport vodReport = new VodReport(reportedVod, reporterId, description);
        reportDao.save(vodReport);

        vodDao.addReportCountAndFlush(vodId,1);
        Vod updatedVod = vodDao.get(vodId);
        return new ReportCountResponse(updatedVod.getId(), updatedVod.getReportCount());
    }

    // TODO Need to use Redis or DynamoDb for concurrency control of many requests
    @Transactional
    public HeartCountResponse addHeartCount(long vodId, int addCount) {
        Vod vod = vodDao.getForUpdate(vodId);
        vod.addHeartCount(addCount);
        return new HeartCountResponse(vod.getId(), vod.getTotalHeartCount());
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
