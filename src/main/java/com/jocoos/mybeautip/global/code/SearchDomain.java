package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.jocoos.mybeautip.global.code.SearchField.COMMENT;
import static com.jocoos.mybeautip.global.code.SearchField.TITLE;

@Getter
@RequiredArgsConstructor
public enum SearchDomain {

    VIDEO(Set.of(TITLE), Set.of(COMMENT));

    private final Set<SearchField> innerFields;
    private final Set<SearchField> outerFields;

    public boolean isOuterField(String searchField) {
        SearchField field = SearchField.get(searchField);
        if (field == null) {
            return false;
        }
        return outerFields.contains(field);
    }
}
