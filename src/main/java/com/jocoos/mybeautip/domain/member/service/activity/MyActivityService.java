package com.jocoos.mybeautip.domain.member.service.activity;

import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.domain.member.dto.MemberActivityRequest;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;

import java.util.List;

public interface MyActivityService<T extends CursorInterface> {
    MemberActivityType getType();
    List<T> getMyActivity(MemberActivityRequest request);
}
