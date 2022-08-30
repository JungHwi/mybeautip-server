package com.jocoos.mybeautip.word;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Hashtable;

@Slf4j
@Service
public class BannedWordService {

    private static final String USERNAME_BANNED_WORD = "username.banned_word";
    private final MessageService messageService;
    private final BannedWordRepository bannedWordRepository;

    public BannedWordService(MessageService messageService,
                             BannedWordRepository bannedWordRepository) {
        this.messageService = messageService;
        this.bannedWordRepository = bannedWordRepository;
    }

    public String findWordAndThrowException(int category, String word, String lang) {
        if (StringUtils.isBlank(word)) {
            return word;
        }

        String lowerCase = word.toLowerCase();
        getDictionary(category).forEach((key, value) -> {
            if (lowerCase.contains(value.getWord().toLowerCase())) {
                throw new BadRequestException("banned_word", messageService.getMessage(USERNAME_BANNED_WORD, lang));
            }
        });

        return word;
    }

    public String findWordAndReplaceWord(String word) {
        // TODO: Find word and replace later

        return word;
    }

    @Cacheable("banned_word")
    public Hashtable<String, BannedWord> getDictionary(int category) {
        Hashtable<String, BannedWord> dictionary = new Hashtable<>();
        bannedWordRepository.findByCategory(category).stream().forEach(
                word -> dictionary.put(word.getWord(), word)
        );
        return dictionary;
    }
}
