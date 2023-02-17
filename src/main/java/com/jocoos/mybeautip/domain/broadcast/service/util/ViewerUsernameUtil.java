package com.jocoos.mybeautip.domain.broadcast.service.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

public class ViewerUsernameUtil {

    private static final String USERNAME_SEPARATOR = "|";
    private static final int REAL_USERNAME_INDEX = 1;

    public enum CharCategory {KOREAN, ENGLISH, NUMBER, SPECIAL_CHARACTERS};
    private static final Set<Integer> KOREAN_CATEGORY = Set.of(5);
    private static final Set<Integer> ENGLISH_CATEGORY = Set.of(1,2);
    private static final Set<Integer> NUMBER_CATEGORY = Set.of(9);

    public static String generateSortedUsername(String username) {
        if (!validateUsername(username)) {
            return username;
        }

        char usernameChar = getUsernameSortedChar(username);

        return String.format("%c%s%s", usernameChar, USERNAME_SEPARATOR, username);
    }

    public static String generateUsername(String sortedUsername) {
        if (!validateSortedUsername(sortedUsername)) {
            return sortedUsername;
        }
        String[] splitUsername = sortedUsername.split(Pattern.quote(USERNAME_SEPARATOR));
        return splitUsername[REAL_USERNAME_INDEX];
    }

    private static char getUsernameSortedChar(String username) {
        char firstChar = username.charAt(0);
        CharCategory category = getCharCategory(firstChar);

        return switch (category) {
            case KOREAN -> '0';
            case ENGLISH -> '1';
            case NUMBER -> '2';
            case SPECIAL_CHARACTERS -> '3';
        };
    }

    private static CharCategory getCharCategory(char firstChar) {
        Integer category = Character.getType(firstChar);

        if (KOREAN_CATEGORY.contains(category)) {
            return CharCategory.KOREAN;
        } else if (ENGLISH_CATEGORY.contains(category)) {
            return CharCategory.ENGLISH;
        } else if (NUMBER_CATEGORY.contains(category)) {
            return CharCategory.NUMBER;
        } else {
            return CharCategory.SPECIAL_CHARACTERS;
        }
    }

    private static boolean validateSortedUsername(String sortedUsername) {
        return StringUtils.isNotBlank(sortedUsername)
                && sortedUsername.contains(USERNAME_SEPARATOR);
    }

    private static boolean validateUsername(String username) {
        return StringUtils.isNotBlank(username)
                && !username.contains(USERNAME_SEPARATOR);
    }
}
