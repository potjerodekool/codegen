package io.github.potjerodekool.codegen.model.util;

import java.util.function.Function;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean hasLength(final String value) {
        return value != null && value.length() > 0;
    }

    public static String firstUpper(final String value) {
        return replaceFirst(value, Character::toUpperCase);
    }

    public static String firstLower(final String value) {
        return replaceFirst(value, Character::toLowerCase);
    }

    private static String replaceFirst(final String value,
                                       final Function<Character, Character> replaceFunction) {
        if (value.length() < 1) {
            return value;
        } else {
            final var first = replaceFunction.apply(value.charAt(0));
            return value.length() == 1
                    ? Character.toString(first)
                    : first + value.substring(1);
        }
    }

}
