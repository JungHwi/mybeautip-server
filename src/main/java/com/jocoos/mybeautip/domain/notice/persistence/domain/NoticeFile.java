package com.jocoos.mybeautip.domain.notice.persistence.domain;

import com.jocoos.mybeautip.domain.file.code.FileType;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NoticeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType type;

    @Column(nullable = false)
    private String file;

    @Setter
    @ManyToOne
    private Notice notice;

    public void editFile(String file) {
        this.file = file;
    }
}
