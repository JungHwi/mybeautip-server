package com.jocoos.mybeautip.domain.placard.persistence.domain;

import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.jocoos.mybeautip.global.code.UrlDirectory.PLACARD;
import static com.jocoos.mybeautip.global.util.ImageFileConvertUtil.toFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "placard_detail")
public class PlacardDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private PlacardTabType tabType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "placard_id", updatable = false)
    Placard placard;

    @Column
    String imageFile;

    public void setPlacard(Placard placard) {
        this.placard = placard;
    }

    public String getImageUrl() {
        return toUrl(imageFile, PLACARD);
    }

    public void replaceFile(String imageUrl) {
        this.imageFile = toFileName(imageUrl);
    }
}
