package com.jocoos.mybeautip.domain.member.dao;

import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberBlockDao {

    private final BlockRepository repository;

    @Transactional(readOnly = true)
    public boolean isBlock(long memberId, long targetId) {

        // FIXME 재훈님 여기도 Repository 에서 조회해서 가져오는 것으로 해주세요.
        return false;
    }

    @Transactional(readOnly = true)
    public List<Block> isBlock(long memberId, List<Long> targetIdList) {

        // FIXME 재훈님 여기도 Repository 에서 조회해서 가져오는 것으로 해주세요.
        return new ArrayList<>();
    }
}
