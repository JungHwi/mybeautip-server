package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.file.code.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private FileType type;

    @Column
    private String file;

    public CommunityFile(FileType type, String file) {
        this.type = type;
        this.file = file;
    }

    public CommunityFile(String file, Community community) {
        this.file = file;
        this.community = community;
    }

    public String getFileUrl() {
        return toUrl(file, COMMUNITY, community.getId());
    }

    public boolean isUrlEqual(String url) {
        return getFileUrl().equals(url);
    }
}
