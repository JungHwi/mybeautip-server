package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.member.block.Block;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.MEMBER_BLOCK;

@Log4j2
@Component
public class MemberBlockMessageConverter implements MessageConverter {

    private static final String BLOCK_MEMBER_FORMAT = "*회원차단*```%d (이)가 %s/%d 회원을 차단함```";

    @Override
    public MessageType getType() {
        return MEMBER_BLOCK;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof Block block) {
            log.debug("memberBlock: {}", block);
            return Optional.ofNullable(
                    String.format(BLOCK_MEMBER_FORMAT,
                            block.getMe(),
                            block.getMemberYou().getUsername(),
                            block.getMemberYou().getId()));
        }
        return Optional.empty();
    }
}
