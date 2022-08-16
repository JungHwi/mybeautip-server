package com.jocoos.mybeautip.domain.member.dao;

import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;


@Service
@RequiredArgsConstructor
public class MemberBlockDao {

    private final BlockRepository repository;

    @Transactional
    public Block save(Block block) {
        return repository.save(block);
    }

    @Transactional(readOnly = true)
    public Block getBlock(Long memberId, Long targetId) {
        return repository.findByMeAndMemberYouId(memberId, targetId).orElseThrow(() ->
                new NotFoundException("block info not found, memberId : " + memberId + " , unblockMemberId : " + targetId)
        );
    }

    @Transactional(readOnly = true)
    public Block getBlockOrElseNewBlock(Long memberId, Member targetMember) {
        return repository.findByMeAndMemberYouId(memberId, targetMember.getId())
                .orElse(new Block(memberId, targetMember));
    }

    @Transactional(readOnly = true)
    public List<Block> findAllMyBlock(Long me) {
        return repository.findByMeAndStatus(me, BLOCK);
    }

    @Transactional(readOnly = true)
    public boolean isBlock(Long memberId, Member targetMember) {
        return repository.countByMeAndMemberYouAndStatus(memberId, targetMember, BLOCK) > 0;
    }

    @Transactional(readOnly = true)
    public boolean isBlock(Long memberId, Long targetMemberId) {
        return repository.existsByMeAndMemberYouIdAndStatus(memberId, targetMemberId, BLOCK);
    }

    @Transactional(readOnly = true)
    public List<Block> isBlock(Long memberId, List<Long> targetMemberIds) {
        return repository.findAllByMeAndMemberYouIdInAndStatus(memberId, targetMemberIds, BLOCK);
    }
}
