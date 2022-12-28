package com.jocoos.mybeautip.domain.notice.persistence.domain;

import com.jocoos.mybeautip.domain.popupnotice.code.NoticeStatus;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notice extends CreatedAtBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<NoticeFile> files;



    @PostPersist
    public void postPersist() {
        if (CollectionUtils.isNotEmpty(files)) {
            files.forEach(file -> file.setNotice(this));
        }
    }
}
