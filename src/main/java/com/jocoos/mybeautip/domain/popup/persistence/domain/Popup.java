package com.jocoos.mybeautip.domain.popup.persistence.domain;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.popup.code.PopupDisplayType;
import com.jocoos.mybeautip.domain.popup.code.PopupStatus;
import com.jocoos.mybeautip.domain.popup.code.PopupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "popup")
public class Popup extends CreatedDateAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private PopupType type;

    @Column
    @Enumerated(EnumType.STRING)
    private PopupStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PopupDisplayType displayType;

    @Column
    private String imageFile;

    @Column
    private String description;

    @Column
    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime endedAt;

    @OneToMany(mappedBy = "popup")
    private List<PopupButton> buttonList;
}
