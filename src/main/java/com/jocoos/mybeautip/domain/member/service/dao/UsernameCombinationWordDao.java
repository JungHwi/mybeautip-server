package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.persistence.domain.UsernameCombinationWord;
import com.jocoos.mybeautip.domain.member.persistence.repository.UsernameCombinationWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsernameCombinationWordDao {

    private final UsernameCombinationWordRepository repository;

    // FIXME Application Cache 말고 외부(redis 나 아니면 redis 같은거) Cache 도입 필요
    @Cacheable(value = "random_username")
    public List<UsernameCombinationWord> findAll() {
        return repository.findAll();
    }

    @CachePut(value = "random_username")
    public List<UsernameCombinationWord> refresh() {
        return repository.findAll();
    }
}
