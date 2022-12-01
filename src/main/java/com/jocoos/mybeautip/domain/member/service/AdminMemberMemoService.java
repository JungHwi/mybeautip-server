package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.dto.MemoResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberMemoDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminMemberMemoService {

    private final MemberDao memberDao;
    private final MemberMemoDao memberMemoDao;

    @Transactional
    public MemoResponse write(String memo, Long memberId, Member createdBy) {
        Member member = memberDao.getMember(memberId);
        MemberMemo memberMemo = new MemberMemo(memo, member, createdBy);
        return new MemoResponse(memberMemoDao.save(memberMemo));
    }

    @Transactional
    public Long edit(Long memoId, Long memberId, String editMemo, Member editedBy) {
        MemberMemo memberMemo = memberMemoDao.get(memoId, memberId);
        memberMemo.edit(editMemo, editedBy);
        return memberMemo.getId();
    }

    @Transactional
    public Long delete(Long memoId, Long memberId, Member deletedBy) {
        MemberMemo memberMemo = memberMemoDao.get(memoId, memberId);
        memberMemo.validSameWriter(deletedBy);
        memberMemoDao.delete(memberMemo);
        return memberMemo.getId();
    }
}
