package com.jocoos.mybeautip.word;

import java.util.Hashtable;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.MemberService;

@Slf4j
@Service
public class BannedWordService {

  private BannedWordRepository bannedWordRepository;

  public BannedWordService(BannedWordRepository bannedWordRepository) {
    this.bannedWordRepository = bannedWordRepository;
  }

  public String findWordAndThrowException(String word) {
    if (Strings.isNullOrEmpty(word)) {
      return word;
    }

    String lowerCase = word.toLowerCase();
    getDictionary(BannedWord.CATEGORY_USERNAME).forEach((key, value) -> {
      if (lowerCase.contains(value.getWord().toLowerCase())) {
        throw new BadRequestException(MemberService.UsernameErrorCode.BANNED_WORD);
      }
    });

    return word;
  }

  public String findWordAndReplaceWord(String word) {
    // TODO: Find word and replace later

    return word;
  }

  @Cacheable("banned_word")
  private Hashtable<String, BannedWord> getDictionary(int category) {
    Hashtable<String, BannedWord> dictionary = new Hashtable<>();
    bannedWordRepository.findByCategory(category).stream().forEach(
       word -> dictionary.put(word.getWord(), word)
    );
    return dictionary;
  }
}
