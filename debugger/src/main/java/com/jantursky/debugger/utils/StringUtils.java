package com.jantursky.debugger.utils;

import java.text.Normalizer;

public final class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    public static boolean isEmpty(String val) {
        return val == null || val.length() == 0 || val.trim().equalsIgnoreCase("") || val.trim().equalsIgnoreCase("null");
    }

    public static String firstLetterUppercase(String text) {
        if (!StringUtils.isEmpty(text)) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    public static String firstLetterLowercase(String text) {
        if (!StringUtils.isEmpty(text)) {
            return text.substring(0, 1).toLowerCase() + text.substring(1);
        }
        return text;
    }

    public static String firstLetterUppercaseOtherLower(String text) {
        if (!StringUtils.isEmpty(text)) {
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
        return text;
    }

    public static String removeAccents(String input) {
        if (!StringUtils.isEmpty(input)) {
            String filterWithoutAccent = Normalizer.normalize(input, Normalizer.Form.NFD);
            filterWithoutAccent = filterWithoutAccent.replaceAll("[^\\p{ASCII}]", "");
            return filterWithoutAccent;
        } else {
            return input;
        }
    }

}