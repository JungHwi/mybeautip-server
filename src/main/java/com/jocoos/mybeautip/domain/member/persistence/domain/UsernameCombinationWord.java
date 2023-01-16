package com.jocoos.mybeautip.domain.member.persistence.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsernameCombinationWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private String word;

    public UsernameCombinationWord(int sequence, String word) {
        this.sequence = sequence;
        this.word = word;
    }
}
