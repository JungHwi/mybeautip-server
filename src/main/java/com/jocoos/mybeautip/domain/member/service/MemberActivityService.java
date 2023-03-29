package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.domain.member.dto.MemberActivityRequest;
import com.jocoos.mybeautip.domain.member.service.activity.MyActivityService;
import com.jocoos.mybeautip.domain.member.service.activity.MyActivityServiceFactory;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberActivityService {

    private final MyActivityServiceFactory myActivityServiceFactory;

    @Transactional(readOnly = true)
    public <T extends CursorInterface> List<T> get(MemberActivityType type,
                                                   Long idCursor,
                                                   int size,
                                                   Member member) {
        MemberActivityRequest request = MemberActivityRequest.builder()
                .idCursor(idCursor)
                .size(size)
                .member(member)
                .build();
        MyActivityService<T> service = myActivityServiceFactory.get(type);
        return service.getMyActivity(request);
    }
}
