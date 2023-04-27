package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.jocoos.mybeautip.global.code.SearchField.*;

@Getter
@RequiredArgsConstructor
public enum SearchDomain {

    VIDEO(Set.of(TITLE), Set.of(COMMENT)),
    BROADCAST(Set.of(TITLE), Set.of()),
    VOD(Set.of(TITLE), Set.of()),
    BRAND(Set.of(NAME, CODE), Set.of())
    ;

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
