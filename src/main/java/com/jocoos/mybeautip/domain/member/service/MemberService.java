package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.persistence.domain.UsernameCombinationWord;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.service.dao.UsernameCombinationWordDao;
import com.jocoos.mybeautip.domain.member.service.social.SocialMemberFactory;
import com.jocoos.mybeautip.global.constant.RegexConstants;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.support.RandomUtils;
import com.jocoos.mybeautip.word.BannedWord;
import com.jocoos.mybeautip.word.BannedWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.exception.ErrorCode.RANDOM_USERNAME_NOT_FOUND;
import static java.util.stream.Collectors.groupingBy;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberService {

    private final BannedWordService bannedWordService;
    private final MemberDao dao;
    private final UsernameCombinationWordDao usernameCombinationWordDao;
    private final MemberRepository memberRepository;
    private final SocialMemberFactory socialMemberFactory;

    @Transactional
    public Member register(SignupRequest signupRequest) {
        Member member = new Member(signupRequest);
        member = adjustUniqueInfo(member);
        Member registeredMember = memberRepository.save(member);

        SocialMemberService socialMemberService = socialMemberFactory.getSocialMemberService(member.getLink());
        socialMemberService.save(signupRequest, member.getId());

        return registeredMember;
    }

    public Member adjustUniqueInfo(Member member) {
        member = adjustTag(member);
        return adjustUserName(member);
    }

    @Transactional(readOnly = true)
    public Member findById(long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("No such member. id - " + id));
    }

    @Transactional(readOnly = true)
    public String generateUsername() {
        String randomUsername = this.getRandomUsername();

        for (int count = 0; count < 10; count++) {
            String randomString = RandomStringUtils.randomAlphanumeric(1).toLowerCase();
            if (!dao.existsByUsername(randomUsername + randomString)) {
                return randomUsername + randomString;
            }
        }

        throw new NotFoundException(RANDOM_USERNAME_NOT_FOUND, "10번 시도 했지만 모두 사용중...");
    }

    private String getRandomUsername() {
        List<UsernameCombinationWord> combinationWordList = usernameCombinationWordDao.findAll();

        Map<Integer, List<UsernameCombinationWord>> wordMap = combinationWordList.stream()
                .collect(groupingBy(UsernameCombinationWord::getSequence));

        List<UsernameCombinationWord> firstList = wordMap.get(1);
        List<UsernameCombinationWord> secondList = wordMap.get(2);

        int firstRandomIndex = RandomUtils.getRandomIndex(firstList.size());
        String firstWord = firstList.get(firstRandomIndex).getWord();

        int secondRandomIndex = RandomUtils.getRandomIndex(secondList.size());
        String secondWord = secondList.get(secondRandomIndex).getWord();

        return String.format("%s%s", firstWord, secondWord);
    }

    private Member adjustTag(Member member) {
        String tag = member.getTag();
        for (int retry = 0; retry < 5; retry++) {
            if (StringUtils.isNotBlank(tag) && !memberRepository.existsByTag(tag)) {
                member.setTag(tag);
                return member;
            }
            tag = RandomUtils.generateTag();
        }

        log.warn("Member is Duplicate Tag. Id : " + member.getId());
        return member;
    }

    public Member adjustUserName(Member member) {
        String username = member.getUsername();
        try {
            checkUsernameValidation(member.getId(), username, Locale.KOREAN.getLanguage());
        } catch (BadRequestException ex) {
            member.setUsername(this.generateUsername());
        }
        return member;
    }

    public void checkUsernameValidation(String username, String lang) {
        checkUsernameValidation(0L, username, lang);
    }

    public void checkUsernameValidation(Long userId, String username, String lang) {
        if (StringUtils.isBlank(username) || username.length() < 2 || username.length() > 10) {
            throw new BadRequestException("username length must be between 2 and 10 characters.");
        }

        if (!(username.matches(RegexConstants.regexForUsername))) {
            throw new BadRequestException(ErrorCode.INVALID_CHAR, "Username does not match the format");
        }

        if (NumberUtils.isDigits(username)) {
            throw new BadRequestException(ErrorCode.INVALID_CHAR_ONLY_DIGIT, "Username does not use only digit.");
        }

        List<Member> otherMembers = memberRepository.findByUsername(username).stream()
                .filter(i -> i.getId() != userId)
                .collect(Collectors.toList());

        if (!otherMembers.isEmpty()) {
            throw new BadRequestException(ErrorCode.ALREADY_USED, "Username already in use");
        }

        bannedWordService.findWordAndThrowException(BannedWord.CATEGORY_USERNAME, username, lang);
    }


}
