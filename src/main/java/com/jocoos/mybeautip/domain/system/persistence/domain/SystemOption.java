package com.jocoos.mybeautip.domain.system.persistence.domain;

import com.jocoos.mybeautip.domain.system.code.SystemOptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class SystemOption {

    @Id
    @Enumerated(EnumType.STRING)
    private SystemOptionType id;

    @Column
    private boolean value;

    public void updateValue(boolean value) {
        this.value = value;
    }
}
