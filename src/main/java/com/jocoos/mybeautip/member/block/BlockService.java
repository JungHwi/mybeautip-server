package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.domain.member.service.dao.MemberBlockDao;
import com.jocoos.mybeautip.domain.slack.aspect.annotation.SendSlack;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.MEMBER_BLOCK;
import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;
import static com.jocoos.mybeautip.member.block.BlockStatus.UNBLOCK;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockService {

    private final MemberBlockDao memberBlockDao;
    private final MemberRepository memberRepository;

    @Transactional
    public Block changeTargetBlockStatus(Long currentMemberId, Long targetId, boolean isBlock) {
        if (isBlock) {
            return blockMember(currentMemberId, targetId);
        } else {
            return unblockMember(currentMemberId, targetId);
        }
    }

    @Transactional
    public Block blockMember(long userId, long targetId) {
        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new MemberNotFoundException("No such target member. id - " + targetId));

        return blockMember(userId, targetMember);
    }

    @SendSlack(messageType = MEMBER_BLOCK)
    @Transactional
    public Block blockMember(long memberId, Member targetMember) {
        Block block = memberBlockDao.getBlockOrElseNewBlock(memberId, targetMember);
        validAlreadyBlocked(block);
        block.changeStatus(BLOCK);
        memberBlockDao.save(block);
        return block;
    }

    @Transactional
    public Block unblockMember(Long memberId, Long unblockMemberId) {
        Block block = memberBlockDao.getBlock(memberId, unblockMemberId);
        block.changeStatus(UNBLOCK);
        return block;
    }

    @Transactional
    public void unblock(Block block) {
        block.changeStatus(UNBLOCK);
        memberBlockDao.save(block);
    }

    private void validAlreadyBlocked(Block block) {
        if (BLOCK.equals(block.getStatus())) {
            throw new BadRequestException(
                    "already blocked, memberId : " + block.getMe() + " targetId :" + block.getYouId());
        }
    }
}
