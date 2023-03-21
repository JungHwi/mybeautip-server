package com.jocoos.mybeautip.domain.slack.converter;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;

import java.util.Optional;

public interface MessageConverter {
    MessageType getType();
    Optional<String> toMessage(Object object);
}
