package com.jocoos.mybeautip.word;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "banned_words")
public class BannedWord {

    public static final int CATEGORY_USERNAME = 1;
    public static final int CATEGORY_BANNED_WORD = 2;
    @Id
    private String word;

    @Column
    private String clean;

    @Column(nullable = false)
    private int category;

    @Column(nullable = false)
    @CreatedDate
    private Date createdAt;

}
