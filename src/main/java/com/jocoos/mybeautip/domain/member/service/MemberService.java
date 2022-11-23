package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.persistence.domain.UsernameCombinationWord;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.service.dao.UsernameCombinationWordDao;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.exception.ErrorCode.RANDOM_USERNAME_NOT_FOUND;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UsernameCombinationWordDao dao;
    private final MemberDao memberDao;
    private final MemberRepository memberRepository;

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
            if (!memberDao.existsByUsername(randomUsername + randomString)) {
                return randomUsername + randomString;
            }
        }

        throw new NotFoundException(RANDOM_USERNAME_NOT_FOUND, "10번 시도 했지만 모두 사용중...");
    }

    @Transactional(readOnly = true)
    public String getRandomUsername() {
        List<UsernameCombinationWord> combinationWordList = dao.findAll();

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
}
