package com.jocoos.mybeautip.domain.slack.aspect;

import com.jocoos.mybeautip.domain.slack.aspect.annotation.SendSlack;
import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverterFactory;
import com.jocoos.mybeautip.support.slack.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Aspect
@Component
public class SlackSendAspect {

    private final SlackService slackService;
    private final MessageConverterFactory converterFactory;

    @AfterReturning(value = "@annotation(sendSlack)", returning = "result")
    public void sendSlack(JoinPoint joinPoint, SendSlack sendSlack, Object result) {

        log.debug("joinPoint: {}", joinPoint.toLongString());

        MessageType messageType = sendSlack.messageType();
        if (result instanceof List<?> results) {
            sendAll(messageType, results);
        } else {
            send(messageType, result);
        }
    }

    private void sendAll(MessageType messageType, List<?> results) {
        for (Object ob : results) {
            send(messageType, ob);
        }
    }

    private void send(MessageType type, Object ob) {
        Optional<MessageConverter> converterOptional = converterFactory.get(type);
        converterOptional.ifPresent(converter -> {
            Optional<String> message = converter.toMessage(ob);
            message.ifPresent(slackService::send);
        });
    }
}
