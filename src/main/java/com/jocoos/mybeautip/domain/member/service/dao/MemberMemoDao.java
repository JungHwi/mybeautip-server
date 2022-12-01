package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberMemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberMemoDao {

    private final MemberMemoRepository repository;

    public MemberMemo save(MemberMemo memberMemo) {
        return repository.save(memberMemo);
    }
}
