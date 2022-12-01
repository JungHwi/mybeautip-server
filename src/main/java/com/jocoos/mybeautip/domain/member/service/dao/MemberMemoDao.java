package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberMemoRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.exception.ErrorCode.MEMBER_MEMO_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class MemberMemoDao {

    private final MemberMemoRepository repository;

    @Transactional
    public MemberMemo save(MemberMemo memberMemo) {
        return repository.save(memberMemo);
    }

    @Transactional(readOnly = true)
    public MemberMemo get(Long memoId, Long memberId) {
        return repository.findByIdAndMemberId(memoId, memberId)
                .orElseThrow(() -> new NotFoundException(MEMBER_MEMO_NOT_FOUND, "memo id - " + memoId + ", member id" + memberId));
    }

    @Transactional
    public void delete(MemberMemo memberMemo) {
        repository.delete(memberMemo);
    }
}
