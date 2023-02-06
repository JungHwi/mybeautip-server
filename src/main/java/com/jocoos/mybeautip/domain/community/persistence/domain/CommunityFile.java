package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.domain.file.code.FileUrlDomain;
import lombok.*;

import javax.persistence.*;

import static com.jocoos.mybeautip.domain.file.code.FileType.VIDEO;
import static com.jocoos.mybeautip.domain.file.code.FileUrlDomain.MYBEAUTIP;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "community_file")
public class CommunityFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @Enumerated(STRING)
    private FileType type;

    @Enumerated(STRING)
    private FileUrlDomain domain;

    @Column
    private String file;

    @Builder
    public CommunityFile(FileType type, FileUrlDomain domain, String file) {
        this.type = type;
        this.domain = domain == null ? MYBEAUTIP : domain;
        this.file = file;
    }

    public CommunityFile(String file, Community community) {
        this.file = file;
        this.community = community;
    }

    public String getFileUrl() {
        return toUrl(domain, file, COMMUNITY, community.getId());
    }

    public boolean isUrlEqual(String url) {
        return getFileUrl().equals(url);
    }

    public void change(FileUrlDomain domain, String fileName) {
        this.domain = domain;
        this.file = fileName;
    }

    public boolean isVideo() {
        return type == VIDEO;
    }
}
