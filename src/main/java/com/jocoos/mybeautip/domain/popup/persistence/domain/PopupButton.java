package com.jocoos.mybeautip.domain.popup.persistence.domain;

import com.jocoos.mybeautip.domain.popup.code.ButtonLinkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "popup_button")
public class PopupButton {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    private ButtonLinkType linkType;

    @Column
    private String linkArgument;

    @ManyToOne
    @JoinColumn(name = "popup_id", insertable = false, updatable = false)
    private Popup popup;

}
