package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Block blockMember(long userId, long targetId) {
        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new MemberNotFoundException("No such target member. id - " + targetId));

        return blockMember(userId, targetMember);
    }

    @Transactional
    public Block blockMember(long userId, Member targetMember) {
        Optional<Block> optional = blockRepository.findByMeAndMemberYouId(userId, targetMember.getId());

        if (optional.isPresent()) {
            return optional.get();
        } else {
            Block block = blockRepository.save(new Block(userId, targetMember));
            return block;
        }
    }

    public Map<Long, Block> getBlackListByMe(Long me) {
        List<Block> blocks = blockRepository.findByMe(me);
        Map<Long, Block> map = blocks != null ?
                blocks.stream().collect(Collectors.toMap(Block::getYouId, Function.identity())) :
                new HashMap<>();
        new HashMap<>();

        if (map.keySet().size() > 0) {
            log.debug("{} blocked by {}", new ArrayList<>(map.keySet()), me);
        }

        return map;
    }

    @Transactional
    public boolean isBlocked(long memberId, long targetId) {
        Member targetMember = memberRepository.findById(targetId)
                .orElseGet(null);

        if (targetMember == null) {
            return false;
        }

        return isBlocked(memberId, targetMember);

    }

    @Transactional
    public boolean isBlocked(long memberId, Member targetMember) {
        return blockRepository.countByMeAndMemberYou(memberId, targetMember) > 0;
    }
}
