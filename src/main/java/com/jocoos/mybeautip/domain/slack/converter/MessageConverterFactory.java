package com.jocoos.mybeautip.domain.slack.converter;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class MessageConverterFactory {

    private final Map<MessageType, MessageConverter> converterMap;

    public MessageConverterFactory(List<MessageConverter> messageConverters) {
        converterMap = messageConverters.stream()
                .collect(toUnmodifiableMap(MessageConverter::getType, Function.identity()));
    }

    public Optional<MessageConverter> get(MessageType type) {
        return Optional.ofNullable(converterMap.get(type));
    }
}
