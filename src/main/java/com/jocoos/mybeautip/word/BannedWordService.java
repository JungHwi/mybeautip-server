package com.jocoos.mybeautip.word;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;

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
    bannedWordRepository.findByCategory(BannedWord.CATEGORY_USERNAME).stream().forEach(
       w -> {
         if (lowerCase.contains(w.getWord().toLowerCase())) {
           throw new BadRequestException("invalid_username", "Your username is not available");
         }
       }
    );
    return word;
  }

  public String findWordAndReplaceWord(String word) {
    return word;
  }
}
