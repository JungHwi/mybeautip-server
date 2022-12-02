package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.domain.term.dto.TermTypeResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MemberMeInfo extends MemberInfo {
    private int point;
    private int revenue;

    private int pointRatio;
    private int revenueRatio;

    private Date revenueModifiedAt;
    private Boolean pushable;
    private List<TermTypeResponse> optionTermAccepts = new ArrayList<>();

    public MemberMeInfo(Member member) {
        BeanUtils.copyProperties(member, this);
        this.setVideoCount(member.getTotalVideoCount());
        this.setPermission(new PermissionInfo(member.getPermission()));
    }

    public MemberMeInfo(Member member, List<TermTypeResponse> responses) {
        this(member);
        this.optionTermAccepts = responses;
    }
}
