package com.jocoos.mybeautip.domain.event.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public class SearchKeyword {
    private final String searchField;
    private final String keyword;

    public static SearchKeyword from(String queryString) {

        if (!StringUtils.hasText(queryString)) {
            return new SearchKeyword(null, null);
        }

        String[] splitQueryString = queryString.split(",");
        return new SearchKeyword(splitQueryString[0], splitQueryString[1]);
    }
}
