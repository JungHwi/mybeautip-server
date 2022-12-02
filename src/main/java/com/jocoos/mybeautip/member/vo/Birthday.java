package com.jocoos.mybeautip.member.vo;

import com.jocoos.mybeautip.support.LocalDateTimeUtils;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

@Getter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Birthday {
    private LocalDate birthday;

    // TODO 현재는 생일을 안 받고 연령대만 받기때문에 역산하는 생성자를 만듬. 생일을 받게 되면 해당 생성자는 삭제.
    public Birthday(int age) {
        this.birthday = LocalDate.now().minusYears(age);
    }

    public Birthday(String birthday) {
        this.birthday = LocalDateTimeUtils.toLocalDate(birthday, LOCAL_DATE_FORMAT);
    }

    public String toString() {
        return LocalDateTimeUtils.toString(birthday);
    }

    public int getAge() {
        if (this.birthday == null) {
            return 0;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public int getAgeGroupByTen() {
        return (getAge() / 10) * 10;
    }
}


