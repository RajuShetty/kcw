package com.kannadachristianwallpapers.app.utils;

import java.util.Locale;

public class StringUtil {
    public static String removeAmp(String s) {
        if (s.contains("amp;")) {
            return s.replace("amp;", "").trim();
        }
        return s.trim();
    }

    public static String addLeadingZero(int integer) {
        return String.format(new Locale("en", "US"), "%02d", integer);
    }
}
