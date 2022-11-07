package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface NativeContentReportRepository extends JpaRepository<CommunityReport, Long> {

    @Query(nativeQuery = true,
            value =
                    "select 'community' as type, community_report.id as id, community_report.member_id as accuserId, members.username as accuserUsername, community_report.description as reason, community_report.created_at as createdAt " +
                            "from community_report join members on community_report.member_id = members.id " +
                            "where community_report.reported_id = :reportedId " +
                            "union all " +
                            "select 'community_comment' as type, community_comment_report.id as id, community_comment_report.member_id as accuserId ,members.username as accuserUsername, community_comment_report.description as reason, community_comment_report.created_at as createdAt " +
                            "from community_comment_report join members on community_comment_report.member_id = members.id " +
                            "where community_comment_report.reported_id = :reportedId " +
                            "union all " +
                            "select 'video_comment' as type, comment_reports.id as id, comment_reports.created_by as accuserId ,m.username as accuserUsername, comment_reports.reason, comment_reports.created_at as createdAt " +
                            "from comment_reports join members m on comment_reports.created_by = m.id " +
                            "where comment_reports.reported_id = :reportedId " +
                            "order by createdAt desc",
            countQuery = "select community_report.member_id as accuserId " +
                    "from community_report " +
                    "where community_report.reported_id = :reportedId " +
                    "union all " +
                    "select community_comment_report.member_id as accuserId " +
                    "from community_comment_report " +
                    "where community_comment_report.reported_id = :reportedId " +
                    "union all " +
                    "select comment_reports.created_by as accuserId " +
                    "from comment_reports " +
                    "where comment_reports.reported_id = :reportedId"
    )
    public Page<Map<String, Object>> unionAllContentReport(@Param("reportedId") Long reportedId, Pageable pageable);
}
